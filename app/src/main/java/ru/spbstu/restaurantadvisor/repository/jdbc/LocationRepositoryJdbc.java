package ru.spbstu.restaurantadvisor.repository.jdbc;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.spbstu.restaurantadvisor.model.Location;
import ru.spbstu.restaurantadvisor.repository.LocationRepository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

@Repository
public class LocationRepositoryJdbc implements LocationRepository {
    
    private final JdbcTemplate jdbcTemplate;
    
    public LocationRepositoryJdbc(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void upsert(Location location) {
        String sql = """
            INSERT INTO locations (telegram_id, latitude, longitude, city, updated_at)
            VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)
            ON CONFLICT (telegram_id) DO UPDATE
            SET latitude = EXCLUDED.latitude,
                longitude = EXCLUDED.longitude,
                city = EXCLUDED.city,
                updated_at = CURRENT_TIMESTAMP
            """;
        
        jdbcTemplate.update(sql, 
            location.telegramId(),
            location.latitude(),
            location.longitude(),
            location.city()
        );
    }

    @Override
    public Optional<Location> findByTelegramId(long telegramId) {
        String sql = "SELECT telegram_id, latitude, longitude, city FROM locations WHERE telegram_id = ?";
        try {
            Location location = jdbcTemplate.queryForObject(sql, this::mapRow, telegramId);
            return Optional.ofNullable(location);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private Location mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Location(
            rs.getLong("telegram_id"),
            rs.getDouble("latitude"),
            rs.getDouble("longitude"),
            rs.getString("city")
        );
    }
}
