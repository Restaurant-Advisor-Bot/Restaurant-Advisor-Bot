package ru.spbstu.restaurantadvisor.repository.jdbc;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.spbstu.restaurantadvisor.model.HistoryEntry;
import ru.spbstu.restaurantadvisor.repository.HistoryRepository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;

@Repository
public class HistoryRepositoryJdbc implements HistoryRepository {
    
    private final JdbcTemplate jdbcTemplate;
    
    public HistoryRepositoryJdbc(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public HistoryEntry save(HistoryEntry entry) {
        String sql = """
            INSERT INTO history (telegram_id, restaurant_name, address, cuisine, visited_at)
            VALUES (?, ?, ?, ?, ?)
            """;
        
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, entry.telegramId());
            ps.setString(2, entry.restaurantName());
            ps.setString(3, entry.address());
            ps.setString(4, entry.cuisine());
            ps.setTimestamp(5, Timestamp.valueOf(entry.visitedAt()));
            return ps;
        }, keyHolder);
        
        long id = keyHolder.getKey().longValue();
        return new HistoryEntry(id, entry.telegramId(), entry.restaurantName(), 
                                entry.address(), entry.cuisine(), entry.visitedAt());
    }

    @Override
    public List<HistoryEntry> findLastByTelegramId(long telegramId, int limit) {
        String sql = """
            SELECT history_record_id, telegram_id, restaurant_name, address, cuisine, visited_at
            FROM history
            WHERE telegram_id = ?
            ORDER BY visited_at DESC
            LIMIT ?
            """;
        return jdbcTemplate.query(sql, this::mapRow, telegramId, limit);
    }

    private HistoryEntry mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new HistoryEntry(
            rs.getLong("id"),
            rs.getLong("telegram_id"),
            rs.getString("restaurant_name"),
            rs.getString("address"),
            rs.getString("cuisine"),
            rs.getTimestamp("visited_at").toLocalDateTime()
        );
    }
}