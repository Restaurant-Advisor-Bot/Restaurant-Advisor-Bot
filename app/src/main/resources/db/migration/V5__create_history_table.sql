-- V5: Создание таблицы истории посещений
CREATE TABLE history (
    history_record_id BIGSERIAL PRIMARY KEY,
    telegram_id BIGINT NOT NULL,
    restaurant_name VARCHAR(200) NOT NULL,
    address TEXT,
    cuisine VARCHAR(100),
    visited_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_history_users 
        FOREIGN KEY (telegram_id) 
        REFERENCES users(telegram_id) 
        ON DELETE CASCADE
);

-- Индексы
CREATE INDEX idx_history_telegram_visited_at ON history(telegram_id, visited_at DESC);

-- Комментарии
COMMENT ON TABLE history IS 'История посещенных ресторанов';
COMMENT ON COLUMN history.history_record_id IS 'Уникальный идентификатор записи';
COMMENT ON COLUMN history.telegram_id IS 'ID пользователя';
COMMENT ON COLUMN history.restaurant_name IS 'Название ресторана';
COMMENT ON COLUMN history.address IS 'Адрес ресторана';
COMMENT ON COLUMN history.cuisine IS 'Тип кухни';
COMMENT ON COLUMN history.visited_at IS 'Дата посещения';