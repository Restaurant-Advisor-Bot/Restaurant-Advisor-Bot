-- V1: Создание таблицы пользователей
CREATE TABLE users (
    telegram_id BIGINT PRIMARY KEY,
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    registered_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT users_role_check CHECK (role IN ('USER', 'ADMIN'))
);

-- Комментарии
COMMENT ON TABLE users IS 'Пользователи бота';
COMMENT ON COLUMN users.telegram_id IS 'Telegram ID пользователя (первичный ключ)';
COMMENT ON COLUMN users.role IS 'Роль пользователя (USER или ADMIN)';
COMMENT ON COLUMN users.registered_at IS 'Дата и время регистрации';
