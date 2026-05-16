-- V6: Создание таблицы статусов запросов
CREATE TABLE request_status (
    request_id BIGSERIAL PRIMARY KEY,
    telegram_id BIGINT NOT NULL,
    request_type VARCHAR(50) NOT NULL,
    reference_id VARCHAR(100) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_request_status_users 
        FOREIGN KEY (telegram_id) 
        REFERENCES users(telegram_id) 
        ON DELETE CASCADE,
    CONSTRAINT request_status_type_check CHECK (request_type IN ('SEARCH', 'EXPLANATION')),
    CONSTRAINT request_status_status_check CHECK (status IN ('PENDING', 'COMPLETED', 'FAILED'))
);

-- Индексы
CREATE INDEX idx_request_status_telegram_type_status ON request_status(telegram_id, request_type, status);
CREATE UNIQUE INDEX idx_request_status_unique_active ON request_status(telegram_id, reference_id, request_type) 
    WHERE status = 'PENDING';

-- Комментарии
COMMENT ON TABLE request_status IS 'Статусы длительных операций (поиск, генерация объяснений)';
COMMENT ON COLUMN request_status.request_id IS 'Уникальный идентификатор запроса';
COMMENT ON COLUMN request_status.telegram_id IS 'ID пользователя';
COMMENT ON COLUMN request_status.request_type IS 'Тип операции (SEARCH, EXPLANATION)';
COMMENT ON COLUMN request_status.reference_id IS 'ID ресурса (requestId или restaurantId)';
COMMENT ON COLUMN request_status.status IS 'Статус (PENDING, COMPLETED, FAILED)';
COMMENT ON COLUMN request_status.created_at IS 'Дата создания';
COMMENT ON COLUMN request_status.updated_at IS 'Дата обновления';
