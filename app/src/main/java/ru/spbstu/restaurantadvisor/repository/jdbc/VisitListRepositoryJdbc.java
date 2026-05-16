package ru.spbstu.restaurantadvisor.repository.jdbc;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.spbstu.restaurantadvisor.model.VisitListItem;
import ru.spbstu.restaurantadvisor.repository.VisitListRepository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class VisitListRepositoryJdbc implements VisitListRepository {
    
    private final JdbcTemplate jdbcTemplate;
    
    public VisitListRepositoryJdbc(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public VisitListItem save(VisitListItem item) {
        String sql = """
            INSERT INTO visit_list (telegram_id, restaurant_name, address, cuisine, visited)
            VALUES (?, ?, ?, ?, ?)
            """;
        
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, item.telegramId());
            ps.setString(2, item.restaurantName());
            ps.setString(3, item.address());
            ps.setString(4, item.cuisine());
            ps.setBoolean(5, item.visited());
            return ps;
        }, keyHolder);
        
        long id = keyHolder.getKey().longValue();
        return new VisitListItem(id, item.telegramId(), item.restaurantName(), 
                                 item.address(), item.cuisine(), item.visited());
    }

    @Override
    public Optional<VisitListItem> findByIdAndTelegramId(long id, long telegramId) {
        String sql = """
            SELECT visit_record_id, telegram_id, restaurant_name, address, cuisine, visited
            FROM visit_list
            WHERE visit_record_id = ? AND telegram_id = ?
            """;
        try {
            VisitListItem item = jdbcTemplate.queryForObject(sql, this::mapRow, id, telegramId);
            return Optional.ofNullable(item);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public List<VisitListItem> findAllByTelegramId(long telegramId) {
        String sql = """
            SELECT visit_record_id, telegram_id, restaurant_name, address, cuisine, visited
            FROM visit_list
            WHERE telegram_id = ?
            ORDER BY created_at DESC
            """;
        return jdbcTemplate.query(sql, this::mapRow, telegramId);
    }

    @Override
    public int deleteByIdAndTelegramId(long id, long telegramId) {
        String sql = "DELETE FROM visit_list WHERE visit_record_id = ? AND telegram_id = ?";
        return jdbcTemplate.update(sql, id, telegramId);
    }

    @Override
    public int countByTelegramId(long telegramId) {
        String sql = "SELECT COUNT(*) FROM visit_list WHERE telegram_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, telegramId);
        return count != null ? count : 0;
    }

    private VisitListItem mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new VisitListItem(
            rs.getLong("visit_record_id"),
            rs.getLong("telegram_id"),
            rs.getString("restaurant_name"),
            rs.getString("address"),
            rs.getString("cuisine"),
            rs.getBoolean("visited")
        );
    }
}
