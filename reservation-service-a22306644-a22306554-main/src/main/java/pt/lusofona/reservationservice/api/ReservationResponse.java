package pt.lusofona.reservationservice.api;

import java.time.Instant;
import java.util.UUID;

public record ReservationResponse(UUID id, UUID restaurantId, String status, Instant createdAt) {}
