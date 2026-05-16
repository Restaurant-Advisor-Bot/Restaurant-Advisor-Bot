package ru.spbstu.restaurantadvisor.repository.jdbc;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.spbstu.restaurantadvisor.model.RequestStatus;
import ru.spbstu.restaurantadvisor.repository.RequestStatusRepository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

@Repository
public class RequestStatusRepositoryJdbc implements RequestStatusRepository {
    
    private final JdbcTemplate jdbcTemplate;
    
    public RequestStatusRepositoryJdbc(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<RequestStatus> findActiveGeneration(long telegramId, String restaurantId) {
        String sql = """
            SELECT request_id, telegram_id, request_type, reference_id, status
            FROM request_status
            WHERE telegram_id = ? 
              AND reference_id = ? 
              AND request_type = 'EXPLANATION'
              AND status = 'PENDING'
            """;
        try {
            RequestStatus status = jdbcTemplate.queryForObject(sql, this::mapRow, telegramId, restaurantId);
            return Optional.ofNullable(status);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public RequestStatus save(RequestStatus status) {
        String sql = """
            INSERT INTO request_status (telegram_id, request_type, reference_id, status)
            VALUES (?, ?, ?, ?)
            """;
        
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, status.telegramId());
            ps.setString(2, status.requestType());
            ps.setString(3, status.referenceId());
            ps.setString(4, status.status());
            return ps;
        }, keyHolder);
        
        long id = keyHolder.getKey().longValue();
        return new RequestStatus(id, status.telegramId(), status.requestType(), 
                                 status.referenceId(), status.status());
    }

    @Override
    public void updateStatus(long id, String newStatus) {
        String sql = "UPDATE request_status SET status = ?, updated_at = CURRENT_TIMESTAMP WHERE request_id = ?";
        jdbcTemplate.update(sql, newStatus, id);
    }

    private RequestStatus mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new RequestStatus(
            rs.getLong("request_id"),
            rs.getLong("telegram_id"),
            rs.getString("request_type"),
            rs.getString("reference_id"),
            rs.getString("status")
        );
    }
}
