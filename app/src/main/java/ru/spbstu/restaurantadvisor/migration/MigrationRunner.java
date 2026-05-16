package ru.spbstu.restaurantadvisor.migration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Comparator;

@Component
public class MigrationRunner {
    
    private static final Logger logger = LoggerFactory.getLogger(MigrationRunner.class);
    private static final String MIGRATION_PATTERN = "classpath:db/migration/V*.sql";
    
    private final JdbcTemplate jdbcTemplate;
    
    public MigrationRunner(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    public void init() {
        runMigrations();
    }
    
    public void runMigrations() {
        try {
            logger.info("Начало применения миграций");
            
            // Создание таблицы для отслеживания миграций
            createMigrationTable();
            
            // Поиск файлов миграций
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources(MIGRATION_PATTERN);
            
            // Сортировка миграций по имени (V1, V2, V3, ...)
            Arrays.sort(resources, Comparator.comparing(Resource::getFilename));
            
            // Применение миграций
            for (Resource resource : resources) {
                String filename = resource.getFilename();
                if (filename != null && !isMigrationApplied(filename)) {
                    applyMigration(resource, filename);
                }
            }
            
            logger.info("Миграции успешно применены");
            
        } catch (Exception e) {
            logger.error("Ошибка при применении миграций", e);
            throw new RuntimeException("Не удалось применить миграции", e);
        }
    }
    
    private void createMigrationTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS schema_migrations (
                id SERIAL PRIMARY KEY,
                filename VARCHAR(255) NOT NULL UNIQUE,
                applied_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
            )
            """;
        jdbcTemplate.execute(sql);
    }
    
    private boolean isMigrationApplied(String filename) {
        String sql = "SELECT COUNT(*) FROM schema_migrations WHERE filename = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, filename);
        return count != null && count > 0;
    }
    
    private void applyMigration(Resource resource, String filename) {
        try {
            logger.info("Применение миграции: {}", filename);
            
            // Чтение содержимого файла миграции
            StringBuilder sqlBuilder = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    sqlBuilder.append(line).append("\n");
                }
            }
            
            String sql = sqlBuilder.toString();
            
            // Выполнение SQL
            jdbcTemplate.execute(sql);
            
            // Сохранение информации о примененной миграции
            String recordSql = "INSERT INTO schema_migrations (filename) VALUES (?)";
            jdbcTemplate.update(recordSql, filename);
            
            logger.info("Миграция {} успешно применена", filename);
            
        } catch (Exception e) {
            logger.error("Ошибка при применении миграции {}", filename, e);
            throw new RuntimeException("Не удалось применить миграцию: " + filename, e);
        }
    }
}