package pt.lusofona.reservationservice.api;

import pt.lusofona.reservationservice.core.ReservationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.lusofona.reservationservice.dto.CreateReservationRequest;
import pt.lusofona.reservationservice.dto.CreateReservationResponse;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateReservationResponse create(@RequestBody CreateReservationRequest request) {
        var created = reservationService.create(request.restaurantId(), request.slotTime());
        return new CreateReservationResponse(created.id(), created.restaurantId(), created.status(), created.createdAt());
    }


    @PostMapping("/{id}/confirm")
    public CreateReservationResponse confirm(@PathVariable("id") UUID id) {
        var updated = reservationService.confirm(id);
        return new CreateReservationResponse(updated.id(), updated.restaurantId(), updated.status(), updated.createdAt());
    }

    @PostMapping("/{id}/cancel")
    public CreateReservationResponse cancel(@PathVariable("id") UUID id) {
        var updated = reservationService.cancel(id);
        return new CreateReservationResponse(updated.id(), updated.restaurantId(), updated.status(), updated.createdAt());
    }

    @GetMapping
    public List<ReservationResponse> list() {
        return reservationService.list().stream()
                .map(r -> new ReservationResponse(r.id(), r.restaurantId(), r.status(), r.createdAt()))
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationResponse> get(@PathVariable UUID id) {
        var r = reservationService.get(id);
        return ResponseEntity.ok(new ReservationResponse(r.id(), r.restaurantId(), r.status(), r.createdAt()));
    }
}
