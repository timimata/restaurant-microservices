package pt.lusofona.restaurantservice.data;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class RestaurantRepository {

    private final JdbcTemplate jdbcTemplate;

    public RestaurantRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /* ========= Exists ========= */
    public boolean exists(UUID id) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM restaurants WHERE id = ?",
                Integer.class,
                id
        );
        return count != null && count > 0;
    }

    /* ========= Insert ========= */
    public void insert(
            UUID id,
            String name,
            String address,
            String city,
            String country,
            String phone,
            String email
    ) {
        jdbcTemplate.update("""
            INSERT INTO restaurants (id, name, address, city, country, phone, email)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """, id, name, address, city, country, phone, email);
    }

    /* ========= Find by ID ========= */
    public Optional<RestaurantRow> findById(UUID id) {
        RowMapper<RestaurantRow> mapper = (ResultSet rs, int rowNum) ->
                mapRow(rs);

        List<RestaurantRow> rows = jdbcTemplate.query(
                """
                SELECT id, name, address, city, country, phone, email
                FROM restaurants
                WHERE id = ?
                """,
                mapper,
                id
        );

        return rows.stream().findFirst();
    }

    /* ========= Find All ========= */
    public List<RestaurantRow> findAll() {
        RowMapper<RestaurantRow> mapper = (ResultSet rs, int rowNum) ->
                mapRow(rs);

        return jdbcTemplate.query(
                """
                SELECT id, name, address, city, country, phone, email
                FROM restaurants
                ORDER BY name
                """,
                mapper
        );
    }

    /* ========= Row Mapper ========= */
    private RestaurantRow mapRow(ResultSet rs) {
        try {
            return new RestaurantRow(
                    UUID.fromString(rs.getString("id")),
                    rs.getString("name"),
                    rs.getString("address"),
                    rs.getString("city"),
                    rs.getString("country"),
                    rs.getString("phone"),
                    rs.getString("email")
            );
        } catch (Exception e) {
            throw new RuntimeException("Erro a mapear restaurante", e);
        }
    }

    /* ========= DTO ========= */
    public record RestaurantRow(
            UUID id,
            String name,
            String address,
            String city,
            String country,
            String phone,
            String email
    ) {}
}
