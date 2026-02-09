package pt.lusofona.reservationservice.events;

import java.time.Instant;

public record EventEnvelope<T>(
        String eventId,
        String eventType,
        String traceId,
        Instant occurredAt,
        T payload
) {}
