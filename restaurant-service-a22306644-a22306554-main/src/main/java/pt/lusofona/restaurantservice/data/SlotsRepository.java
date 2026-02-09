package pt.lusofona.restaurantservice.data;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
public class SlotsRepository {

    private final JdbcTemplate jdbc;

    public SlotsRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public void insert(UUID restaurantId, Instant slotTime, int capacity) {
        jdbc.update("""
            INSERT INTO restaurant_slots (restaurant_id, slot_time, capacity, available)
            VALUES (?, ?, ?, ?)
            ON CONFLICT (restaurant_id, slot_time)
            DO UPDATE SET capacity = EXCLUDED.capacity, available = EXCLUDED.available
        """, restaurantId, Timestamp.from(slotTime), capacity, capacity);
    }

    public Optional<SlotRow> find(UUID restaurantId, Instant slotTime) {
        RowMapper<SlotRow> mapper = (rs, n) -> new SlotRow(
                UUID.fromString(rs.getString("restaurant_id")),
                rs.getTimestamp("slot_time").toInstant(),
                rs.getInt("capacity"),
                rs.getInt("available")
        );

        var list = jdbc.query("""
            SELECT restaurant_id, slot_time, capacity, available
            FROM restaurant_slots
            WHERE restaurant_id = ? AND slot_time = ?
        """, mapper, restaurantId, Timestamp.from(slotTime));

        return list.stream().findFirst();
    }

    // Lock row + decrement if available > 0
    public int hold(UUID restaurantId, Instant slotTime) {
        return jdbc.update("""
            UPDATE restaurant_slots
            SET available = available - 1
            WHERE restaurant_id = ? AND slot_time = ? AND available > 0
        """, restaurantId, Timestamp.from(slotTime));
    }

    public int release(UUID restaurantId, Instant slotTime) {
        return jdbc.update("""
            UPDATE restaurant_slots
            SET available = available + 1
            WHERE restaurant_id = ? AND slot_time = ? AND available < capacity
        """, restaurantId, Timestamp.from(slotTime));
    }

    public record SlotRow(UUID restaurantId, Instant slotTime, int capacity, int available) {}
}
