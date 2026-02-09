package pt.lusofona.reservationservice.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class ReservationEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public ReservationEventProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void publishCreated(UUID reservationId, UUID restaurantId) {
        publish("reservation.created", reservationId, restaurantId, "PENDING");
    }

    public void publishConfirmed(UUID reservationId, UUID restaurantId) {
        publish("reservation.confirmed", reservationId, restaurantId, "CONFIRMED");
    }

    public void publishCancelled(UUID reservationId, UUID restaurantId) {
        publish("reservation.cancelled", reservationId, restaurantId, "CANCELLED");
    }

    private void publish(String eventType, UUID reservationId, UUID restaurantId, String status) {
        var payload = new ReservationEventPayload(reservationId, restaurantId, status);

        var envelope = new EventEnvelope<>(
                UUID.randomUUID().toString(),      // eventId
                eventType,                         // eventType
                UUID.randomUUID().toString(),      // traceId (por agora)
                Instant.now(),                     // occurredAt
                payload                            // payload
        );

        try {
            String json = objectMapper.writeValueAsString(envelope);

            // ✅ PASSO 1.2: key = reservationId
            kafkaTemplate.send(eventType, reservationId.toString(), json);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
