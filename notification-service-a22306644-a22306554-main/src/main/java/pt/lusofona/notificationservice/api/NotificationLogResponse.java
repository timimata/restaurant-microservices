package pt.lusofona.notificationservice.api;

import java.time.Instant;
import java.util.UUID;

public record NotificationLogResponse(
        UUID id,
        String eventId,
        String eventType,
        Instant createdAt
) {}
