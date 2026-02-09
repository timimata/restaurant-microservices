package     pt.lusofona.reservationservice.dto;

import java.time.Instant;
import java.util.UUID;

public record CreateReservationResponse(
        UUID id,
        UUID restaurantId,
        String status,
        Instant createdAt
) {}
