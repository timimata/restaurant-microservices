package pt.lusofona.restaurantservice.data;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class MenuItemsRepository {

    private final JdbcTemplate jdbc;

    public MenuItemsRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public void insert(UUID id, UUID restaurantId, String name, String description, BigDecimal price, String currency) {
        jdbc.update("""
            INSERT INTO menu_items (id, restaurant_id, name, description, price, currency)
            VALUES (?, ?, ?, ?, ?, ?)
        """, id, restaurantId, name, description, price, currency);
    }

    public Optional<MenuItemRow> findById(UUID id) {
        RowMapper<MenuItemRow> mapper = (rs, n) -> new MenuItemRow(
                UUID.fromString(rs.getString("id")),
                UUID.fromString(rs.getString("restaurant_id")),
                rs.getString("name"),
                rs.getString("description"),
                rs.getBigDecimal("price"),
                rs.getString("currency")
        );

        List<MenuItemRow> rows = jdbc.query("""
            SELECT id, restaurant_id, name, description, price, currency
            FROM menu_items
            WHERE id = ?
        """, mapper, id);

        return rows.stream().findFirst();
    }

    public List<MenuItemRow> findByRestaurantId(UUID restaurantId) {
        RowMapper<MenuItemRow> mapper = (rs, n) -> new MenuItemRow(
                UUID.fromString(rs.getString("id")),
                UUID.fromString(rs.getString("restaurant_id")),
                rs.getString("name"),
                rs.getString("description"),
                rs.getBigDecimal("price"),
                rs.getString("currency")
        );

        return jdbc.query("""
            SELECT id, restaurant_id, name, description, price, currency
            FROM menu_items
            WHERE restaurant_id = ?
            ORDER BY name
        """, mapper, restaurantId);
    }

    public int update(UUID id, String name, String description, BigDecimal price, String currency) {
        return jdbc.update("""
        UPDATE menu_items
        SET name = ?, description = ?, price = ?, currency = ?
        WHERE id = ?
    """, name, description, price, currency, id);
    }

    public int deleteById(UUID id) {
        return jdbc.update("DELETE FROM menu_items WHERE id = ?", id);
    }


    public record MenuItemRow(
            UUID id,
            UUID restaurantId,
            String name,
            String description,
            BigDecimal price,
            String currency
    ) {}
}
