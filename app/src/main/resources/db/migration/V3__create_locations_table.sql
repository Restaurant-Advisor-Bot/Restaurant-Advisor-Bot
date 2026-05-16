-- V3: Создание таблицы местоположений
CREATE TABLE locations (
    telegram_id BIGINT PRIMARY KEY,
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    city VARCHAR(100),
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_locations_users 
        FOREIGN KEY (telegram_id) 
        REFERENCES users(telegram_id) 
        ON DELETE CASCADE
);

-- Комментарии
COMMENT ON TABLE locations IS 'Текущее местоположение пользователей';
COMMENT ON COLUMN locations.telegram_id IS 'ID пользователя (первичный ключ)';
COMMENT ON COLUMN locations.latitude IS 'Широта';
COMMENT ON COLUMN locations.longitude IS 'Долгота';
COMMENT ON COLUMN locations.city IS 'Название города (может быть NULL)';
COMMENT ON COLUMN locations.updated_at IS 'Дата последнего обновления';
