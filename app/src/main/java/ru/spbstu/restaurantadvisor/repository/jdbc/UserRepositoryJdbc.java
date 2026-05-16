package ru.spbstu.restaurantadvisor.repository.jdbc;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.spbstu.restaurantadvisor.model.User;
import ru.spbstu.restaurantadvisor.repository.UserRepository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class UserRepositoryJdbc implements UserRepository {
    
    private final JdbcTemplate jdbcTemplate;
    
    public UserRepositoryJdbc(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User save(User user) {
        String sql = """
            INSERT INTO users (telegram_id, role, registered_at)
            VALUES (?, ?, ?)
            ON CONFLICT (telegram_id) DO NOTHING
            RETURNING telegram_id, role, registered_at
            """;
        
        try {
            return jdbcTemplate.queryForObject(sql, this::mapRow, 
                user.telegramId(), user.role(), user.registeredAt());
        } catch (Exception e) {
            // Если пользователь уже существует, возвращаем существующего
            return findByTelegramId(user.telegramId()).orElse(user);
        }
    }

    @Override
    public Optional<User> findByTelegramId(long telegramId) {
        String sql = "SELECT telegram_id, role, registered_at FROM users WHERE telegram_id = ?";
        try {
            User user = jdbcTemplate.queryForObject(sql, this::mapRow, telegramId);
            return Optional.ofNullable(user);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public List<User> findAll() {
        String sql = "SELECT telegram_id, role, registered_at FROM users ORDER BY registered_at DESC";
        return jdbcTemplate.query(sql, this::mapRow);
    }

    @Override
    public List<User> findByRole(String role) {
        String sql = "SELECT telegram_id, role, registered_at FROM users WHERE role = ? ORDER BY registered_at DESC";
        return jdbcTemplate.query(sql, this::mapRow, role);
    }

    @Override
    public void updateRole(long telegramId, String role) {
        String sql = "UPDATE users SET role = ? WHERE telegram_id = ?";
        jdbcTemplate.update(sql, role, telegramId);
    }

    private User mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new User(
            rs.getLong("telegram_id"),
            rs.getString("role"),
            rs.getTimestamp("registered_at").toLocalDateTime()
        );
    }
}
