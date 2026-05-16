-- V4: Создание таблицы списка "Посетить"
CREATE TABLE visit_list (
    visit_record_id BIGSERIAL PRIMARY KEY,
    telegram_id BIGINT NOT NULL,
    restaurant_name VARCHAR(200) NOT NULL,
    address TEXT,
    cuisine VARCHAR(100),
    visited BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_visit_list_users 
        FOREIGN KEY (telegram_id) 
        REFERENCES users(telegram_id) 
        ON DELETE CASCADE
);

-- Индексы
CREATE INDEX idx_visit_list_telegram_visited ON visit_list(telegram_id, visited);

-- Комментарии
COMMENT ON TABLE visit_list IS 'Список ресторанов для посещения';
COMMENT ON COLUMN visit_list.visit_record_id IS 'Уникальный идентификатор записи';
COMMENT ON COLUMN visit_list.telegram_id IS 'ID пользователя';
COMMENT ON COLUMN visit_list.restaurant_name IS 'Название ресторана';
COMMENT ON COLUMN visit_list.address IS 'Адрес ресторана';
COMMENT ON COLUMN visit_list.cuisine IS 'Тип кухни';
COMMENT ON COLUMN visit_list.visited IS 'Отметка о посещении';
COMMENT ON COLUMN visit_list.created_at IS 'Дата добавления в список';
