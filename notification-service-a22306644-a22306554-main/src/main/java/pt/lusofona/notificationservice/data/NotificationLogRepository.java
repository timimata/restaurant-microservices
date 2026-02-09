package pt.lusofona.notificationservice.data;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.List;
import java.util.UUID;

@Repository
public class NotificationLogRepository {

    private final JdbcTemplate jdbcTemplate;

    public NotificationLogRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<NotificationLogRow> findLatest(int limit) {
        RowMapper<NotificationLogRow> mapper = (ResultSet rs, int rowNum) -> new NotificationLogRow(
                UUID.fromString(rs.getString("id")),
                rs.getString("event_id"),
                rs.getString("event_type"),
                rs.getTimestamp("created_at").toInstant()
        );

        return jdbcTemplate.query("""
            SELECT id, event_id, event_type, created_at
            FROM notification_log
            ORDER BY created_at DESC
            LIMIT ?
        """, mapper, limit);
    }

    public record NotificationLogRow(UUID id, String eventId, String eventType, java.time.Instant createdAt) {}
}
