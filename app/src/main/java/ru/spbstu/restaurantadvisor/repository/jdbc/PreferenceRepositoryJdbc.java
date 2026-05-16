package ru.spbstu.restaurantadvisor.repository.jdbc;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.spbstu.restaurantadvisor.model.Preference;
import ru.spbstu.restaurantadvisor.repository.PreferenceRepository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class PreferenceRepositoryJdbc implements PreferenceRepository {
    
    private final JdbcTemplate jdbcTemplate;
    
    public PreferenceRepositoryJdbc(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Preference> findByTelegramId(long telegramId) {
        String sql = "SELECT preference_id, telegram_id, text FROM preferences WHERE telegram_id = ? ORDER BY created_at ASC";
        return jdbcTemplate.query(sql, this::mapRow, telegramId);
    }

    @Override
    public Preference save(long telegramId, String text) {
        String sql = "INSERT INTO preferences (telegram_id, text) VALUES (?, ?)";
        
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, telegramId);
            ps.setString(2, text);
            return ps;
        }, keyHolder);
        
        long id = keyHolder.getKey().longValue();
        return new Preference((int) id, telegramId, text);
    }

    @Override
    public int deleteByIdAndTelegramId(long id, long telegramId) {
        String sql = "DELETE FROM preferences WHERE preference_id = ? AND telegram_id = ?";
        return jdbcTemplate.update(sql, id, telegramId);
    }

    @Override
    public Optional<Preference> findByIdAndTelegramId(long id, long telegramId) {
        String sql = "SELECT preference_id, telegram_id, text FROM preferences WHERE preference_id = ? AND telegram_id = ?";
        try {
            Preference preference = jdbcTemplate.queryForObject(sql, this::mapRow, id, telegramId);
            return Optional.ofNullable(preference);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public int countByTelegramId(long telegramId) {
        String sql = "SELECT COUNT(*) FROM preferences WHERE telegram_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, telegramId);
        return count != null ? count : 0;
    }

    private Preference mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Preference(
            rs.getInt("preference_id"),
            rs.getLong("telegram_id"),
            rs.getString("text")
        );
    }
}
