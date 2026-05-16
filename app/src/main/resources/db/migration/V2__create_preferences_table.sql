-- V2: Создание таблицы предпочтений
CREATE TABLE preferences (
    preference_id BIGSERIAL PRIMARY KEY,
    telegram_id BIGINT NOT NULL,
    text TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_preferences_users 
        FOREIGN KEY (telegram_id) 
        REFERENCES users(telegram_id) 
        ON DELETE CASCADE
);

-- Индексы
CREATE INDEX idx_preferences_telegram_id ON preferences(telegram_id);

-- Комментарии
COMMENT ON TABLE preferences IS 'Предпочтения пользователей';
COMMENT ON COLUMN preferences.id IS 'Уникальный идентификатор предпочтения';
COMMENT ON COLUMN preferences.telegram_id IS 'ID пользователя';
COMMENT ON COLUMN preferences.text IS 'Текст предпочтения';
COMMENT ON COLUMN preferences.created_at IS 'Дата создания';
