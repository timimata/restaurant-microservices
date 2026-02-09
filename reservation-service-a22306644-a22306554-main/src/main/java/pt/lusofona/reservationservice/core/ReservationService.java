package pt.lusofona.reservationservice.core;

import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pt.lusofona.reservationservice.client.RestaurantClient;
import pt.lusofona.reservationservice.data.ReservationRepository;
import pt.lusofona.reservationservice.events.ReservationEventProducer;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class ReservationService {

    private final ReservationRepository repository;
    private final ReservationEventProducer producer;
    private final RestaurantClient restaurantClient;

    public ReservationService(
            ReservationRepository repository,
            ReservationEventProducer producer,
            RestaurantClient restaurantClient
    ) {
        this.repository = repository;
        this.producer = producer;
        this.restaurantClient = restaurantClient;
    }

    /**
     * Cria reserva PENDING para um restaurantId + slotTime:
     * 1) valida restaurante (exists)
     * 2) HOLD no restaurant-service (decrementa disponibilidade)
     * 3) grava reserva na DB
     * 4) publica evento reservation.created no Kafka
     */
    public CreatedReservation create(UUID restaurantId, Instant slotTime) {

        if (restaurantId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "restaurantId é obrigatório");
        }
        if (slotTime == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "slotTime é obrigatório");
        }

        // 1) HOLD antes de gravar: garante capacidade e evita overbooking
        try {
            restaurantClient.hold(restaurantId, slotTime);
        } catch (FeignException.NotFound e) {
            // slot não existe
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Slot não existe para este restaurante/horário"
            );
        } catch (FeignException.Conflict e) {
            // sem disponibilidade
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Sem disponibilidade para este slot"
            );
        } catch (FeignException e) {
            // falha de rede/serviço
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Falha a contactar restaurant-service para HOLD"
            );
        }

        // 2) gravar reserva
        UUID id = UUID.randomUUID();
        Instant now = Instant.now();
        String status = "PENDING";


        repository.insert(id, restaurantId, status, now, slotTime);

        // 3) publish
        producer.publishCreated(id, restaurantId);

        return new CreatedReservation(id, restaurantId, status, now, slotTime);
    }

    public CreatedReservation confirm(UUID reservationId) {
        var row = repository.findById(reservationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reserva não existe"));

        if ("CONFIRMED".equals(row.status())) {
            return new CreatedReservation(row.id(), row.restaurantId(), row.status(), row.createdAt(), row.slotTime());
        }
        if ("CANCELLED".equals(row.status())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Não é possível confirmar: reserva está CANCELLED");
        }

        repository.updateStatus(reservationId, "CONFIRMED");
        producer.publishConfirmed(row.id(), row.restaurantId());

        return new CreatedReservation(row.id(), row.restaurantId(), "CONFIRMED", row.createdAt(), row.slotTime());
    }

    /**
     * Cancel:
     * 1) update status CANCELLED
     * 2) release vaga no restaurant-service (best-effort)
     * 3) publish cancelled
     */
    public CreatedReservation cancel(UUID reservationId) {
        var row = repository.findById(reservationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reserva não existe"));

        if ("CANCELLED".equals(row.status())) {
            return new CreatedReservation(row.id(), row.restaurantId(), row.status(), row.createdAt(), row.slotTime());
        }

        repository.updateStatus(reservationId, "CANCELLED");

        // release best-effort (não queremos 500 se o restaurant falhar)
        try {
            restaurantClient.release(row.restaurantId(), row.slotTime());
        } catch (FeignException ignored) {
            // opcional: log.warn(...)
        }

        producer.publishCancelled(row.id(), row.restaurantId());

        return new CreatedReservation(row.id(), row.restaurantId(), "CANCELLED", row.createdAt(), row.slotTime());
    }

    public List<CreatedReservation> list() {
        return repository.findAll().stream()
                .map(r -> new CreatedReservation(r.id(), r.restaurantId(), r.status(), r.createdAt(), r.slotTime()))
                .toList();
    }

    public CreatedReservation get(UUID id) {
        var row = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reserva não existe"));
        return new CreatedReservation(row.id(), row.restaurantId(), row.status(), row.createdAt(), row.slotTime());
    }

    public record CreatedReservation(UUID id, UUID restaurantId, String status, Instant createdAt, Instant slotTime) {}
}
