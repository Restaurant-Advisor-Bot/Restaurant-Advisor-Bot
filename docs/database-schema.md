# Описание схемы базы данных Restaurant Advisor Bot

## Таблицы

### 1. users
Хранение информации о пользователях

| Колонка | Тип | Ограничения | Описание |
|---------|-----|-------------|----------|
| telegram_id | BIGINT | PRIMARY KEY | Telegram ID пользователя |
| role | VARCHAR(20) | NOT NULL, DEFAULT 'USER' | Роль (USER, ADMIN) |
| registered_at | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Дата регистрации |

**Индексы:**
- PRIMARY KEY (telegram_id)

**Примечания:**
- telegram_id используется как первичный ключ
- При первом обращении создается запись с ролью 'USER'

---

### 2. preferences
Хранение предпочтений пользователей (диетические ограничения, вкусовые предпочтения)

| Колонка       | Тип | Ограничения | Описание |
|---------------|-----|-------------|----------|
| preference_id | BIGSERIAL | PRIMARY KEY | Уникальный идентификатор предпочтения |
| telegram_id   | BIGINT | NOT NULL, FOREIGN KEY → users(telegram_id) ON DELETE CASCADE | ID пользователя |
| text          | TEXT | NOT NULL | Текст предпочтения |
| created_at    | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Дата создания |

**Индексы:**
- PRIMARY KEY (preference_id)
- INDEX (telegram_id) - для быстрого поиска предпочтений пользователя

**Ограничения:**
- CHECK: максимум 10 предпочтений на пользователя 

**Примечания:**
- Связь с users через telegram_id
- При удалении пользователя удаляются все его предпочтения (CASCADE)

---

### 3. locations
Хранение текущего местоположения пользователей

| Колонка | Тип | Ограничения | Описание |
|---------|-----|-------------|----------|
| telegram_id | BIGINT | PRIMARY KEY, FOREIGN KEY → users(telegram_id) ON DELETE CASCADE | ID пользователя |
| latitude | DOUBLE PRECISION | NOT NULL | Широта |
| longitude | DOUBLE PRECISION | NOT NULL | Долгота |
| city | VARCHAR(100) | NULL | Название города |
| updated_at | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Дата последнего обновления |

**Индексы:**
- PRIMARY KEY (telegram_id)

**Примечания:**
- Один пользователь имеет одно текущее местоположение
- При удалении пользователя удаляется его местоположение (CASCADE)
- city может быть NULL, если местоположение установлено через геолокацию

---

### 4. visit_list
Список ресторанов для посещения

| Колонка         | Тип | Ограничения | Описание |
|-----------------|-----|-------------|----------|
| visit_record_id | BIGSERIAL | PRIMARY KEY | Уникальный идентификатор записи |
| telegram_id     | BIGINT | NOT NULL, FOREIGN KEY → users(telegram_id) ON DELETE CASCADE | ID пользователя |
| restaurant_name | VARCHAR(200) | NOT NULL | Название ресторана |
| address         | TEXT | NULL | Адрес |
| cuisine         | VARCHAR(100) | NULL | Тип кухни |
| visited         | BOOLEAN | NOT NULL, DEFAULT FALSE | Отметка о посещении |
| created_at      | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Дата добавления в список |

**Индексы:**
- PRIMARY KEY (visit_record_id)
- INDEX (telegram_id, visited) - для быстрого поиска непосещенных ресторанов

**Ограничения:**
- CHECK: максимум 50 записей на пользователя (проверяется на уровне приложения)

**Примечания:**
- При удалении пользователя удаляются все записи из его списка (CASCADE)
- Поле visited используется для отметки посещенных ресторанов

---

### 5. history
История посещенных ресторанов

| Колонка           | Тип | Ограничения | Описание |
|-------------------|-----|-------------|----------|
| history_record_id | BIGSERIAL | PRIMARY KEY | Уникальный идентификатор записи |
| telegram_id       | BIGINT | NOT NULL, FOREIGN KEY → users(telegram_id) ON DELETE CASCADE | ID пользователя |
| restaurant_name   | VARCHAR(200) | NOT NULL | Название ресторана |
| address           | TEXT | NULL | Адрес |
| cuisine           | VARCHAR(100) | NULL | Тип кухни |
| visited_at        | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Дата посещения |

**Индексы:**
- PRIMARY KEY (history_record_id)
- INDEX (telegram_id, visited_at DESC) - для быстрого получения последних посещений

**Примечания:**
- При удалении пользователя удаляются все записи из его истории (CASCADE)
- Записи добавляются при отметке ресторана как посещенного из visit_list
- Возвращаются последние 50 записей для команды /history

---

### 6. request_status
Статусы длительных операций (поиск, генерация объяснений)

| Колонка      | Тип | Ограничения | Описание |
|--------------|-----|-------------|----------|
| request_id   | BIGSERIAL | PRIMARY KEY | Уникальный идентификатор запроса |
| telegram_id  | BIGINT | NOT NULL, FOREIGN KEY → users(telegram_id) ON DELETE CASCADE | ID пользователя |
| request_type | VARCHAR(50) | NOT NULL | Тип операции (SEARCH, EXPLANATION) |
| reference_id | VARCHAR(100) | NOT NULL | ID ресурса (requestId или restaurantId) |
| status       | VARCHAR(20) | NOT NULL | Статус (PENDING, COMPLETED, FAILED) |
| created_at   | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Дата создания |
| updated_at   | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Дата обновления |

**Индексы:**
- PRIMARY KEY (request_id)
- INDEX (telegram_id, request_type, status) - для проверки активных запросов
- UNIQUE INDEX (telegram_id, reference_id, request_type) - для предотвращения дублей активных запросов

**Примечания:**
- При удалении пользователя удаляются все его запросы (CASCADE)
- Используется для отслеживания активных операций и предотвращения дублирования
- reference_id для SEARCH - это requestId, для EXPLANATION - это restaurantId

---

## Диаграмма связей

```
users (telegram_id) ──┬─1:N─→ preferences (telegram_id)
                      ├─1:1─→ locations (telegram_id)
                      ├─1:N─→ visit_list (telegram_id)
                      ├─1:N─→ history (telegram_id)
                      └─1:N─→ request_status (telegram_id)
```

## Миграции

Миграции будут создаваться в директории `app/src/main/resources/db/migration/` в следующем порядке:
1. V1__create_users_table.sql
2. V2__create_preferences_table.sql
3. V3__create_locations_table.sql
4. V4__create_visit_list_table.sql
5. V5__create_history_table.sql
6. V6__create_request_status_table.sql