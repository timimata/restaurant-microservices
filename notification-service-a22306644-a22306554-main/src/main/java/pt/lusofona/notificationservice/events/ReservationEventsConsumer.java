package pt.lusofona.notificationservice.events;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

@Component
public class ReservationEventsConsumer {

    private final ObjectMapper objectMapper;
    private final JdbcTemplate jdbcTemplate;

    public ReservationEventsConsumer(ObjectMapper objectMapper, JdbcTemplate jdbcTemplate) {
        this.objectMapper = objectMapper;
        this.jdbcTemplate = jdbcTemplate;
    }

    @KafkaListener(topics = {"reservation.created", "reservation.confirmed", "reservation.cancelled"})
    public void onEvent(String message) throws Exception {
        JsonNode root = objectMapper.readTree(message);

        String eventId = root.get("eventId").asText();
        String eventType = root.get("eventType").asText();

        Instant occurredAt = Instant.now();
        JsonNode occurredAtNode = root.get("occurredAt");
        if (occurredAtNode != null && !occurredAtNode.isNull()) {
            occurredAt = Instant.parse(occurredAtNode.asText());
        }

        JsonNode payload = root.get("payload");


        try {
            jdbcTemplate.update("""
                INSERT INTO notification_log (id, event_id, event_type, created_at)
                VALUES (?, ?, ?, ?)
            """,
                    UUID.randomUUID(),
                    eventId,
                    eventType,
                    Timestamp.from(occurredAt)
            );
        } catch (DataIntegrityViolationException e) {
            // event_id UNIQUE -> já processado (idempotência). Ignorar.
        }
    }
}
