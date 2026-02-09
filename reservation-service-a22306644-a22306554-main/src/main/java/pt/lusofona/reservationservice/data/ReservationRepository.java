package pt.lusofona.reservationservice.data;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ReservationRepository {

    private final JdbcTemplate jdbcTemplate;

    public ReservationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void insert(UUID id, UUID restaurantId, String status, Instant createdAt, Instant slotTime) {
        jdbcTemplate.update("""
            INSERT INTO reservations (id, restaurant_id, status, created_at, slot_time)
            VALUES (?, ?, ?, ?, ?)
        """, id, restaurantId, status, Timestamp.from(createdAt), Timestamp.from(slotTime));
    }

    public Optional<ReservationRow> findById(UUID id) {
        RowMapper<ReservationRow> mapper = (ResultSet rs, int rowNum) -> new ReservationRow(
                UUID.fromString(rs.getString("id")),
                UUID.fromString(rs.getString("restaurant_id")),
                rs.getString("status"),
                rs.getTimestamp("created_at").toInstant(),
                rs.getTimestamp("slot_time").toInstant()
        );

        var list = jdbcTemplate.query("""
            SELECT id, restaurant_id, status, created_at, slot_time
            FROM reservations
            WHERE id = ?
        """, mapper, id);

        return list.stream().findFirst();
    }

    public int updateStatus(UUID id, String newStatus) {
        return jdbcTemplate.update("""
            UPDATE reservations
            SET status = ?
            WHERE id = ?
        """, newStatus, id);
    }

    public java.util.List<ReservationRow> findAll() {
        RowMapper<ReservationRow> mapper = (rs, rowNum) -> new ReservationRow(
                UUID.fromString(rs.getString("id")),
                UUID.fromString(rs.getString("restaurant_id")),
                rs.getString("status"),
                rs.getTimestamp("created_at").toInstant(),
                rs.getTimestamp("slot_time").toInstant()
        );

        return jdbcTemplate.query("""
            SELECT id, restaurant_id, status, created_at, slot_time
            FROM reservations
            ORDER BY created_at DESC
        """, mapper);
    }

    public record ReservationRow(UUID id, UUID restaurantId, String status, Instant createdAt, Instant slotTime) {}
}
