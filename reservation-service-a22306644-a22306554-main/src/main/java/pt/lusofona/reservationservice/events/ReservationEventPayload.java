package pt.lusofona.reservationservice.events;

import java.util.UUID;

public record ReservationEventPayload(
        UUID reservationId,
        UUID restaurantId,
        String status
) {}
