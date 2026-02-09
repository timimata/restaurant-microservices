package pt.lusofona.notificationservice.events;

import java.time.Instant;
import java.util.UUID;

public record ReservationCreatedEvent(
        String eventId,
        String eventType,
        Instant occurredAt,
        UUID reservationId,
        UUID restaurantId
) {}
