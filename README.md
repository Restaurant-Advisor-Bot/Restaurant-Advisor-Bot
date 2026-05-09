# Restaurant Advisor Bot

Telegram-бот для поиска ресторанов с учетом персональных предпочтений и местоположения.

## Требования

Подробное техническое задание и спецификация проекта находятся в папке [`docs/`](docs/):

- [`docs/req.md`](docs/req.md) - требования в формате Markdown.
- [`docs/req.pdf`](docs/req.pdf) - требования в формате PDF.

## Архитектура

Описание архитектуры находится в папке [`docs/`](docs/):

- [`docs/arch.md`](docs/arch.md) - архитектура в формате Markdown.
- [`docs/arch.pdf`](docs/arch.pdf) - архитектура в формате PDF.


## Структура проекта

src\main\java\ru\spbstu\restaurantadvisor \
├───bot - Telegram‑обработчики и отправка сообщений \
│&nbsp; ├───handler - Реализации BotCommandHandler \
│&nbsp; └───sender - Реализации MessageSender \
├───config - Конфигурация Spring (AppConfig) \
├───llm - Клиент LLM (GigaChat) и исключения \
├───model - Объекты бизнес-логики \
├───repository - Интерфейсы репозиториев и реализация \
└───service - Бизнес-логика \
&nbsp;&nbsp;&nbsp;&nbsp;├───explanation - Генерация объяснений (LLM) \
&nbsp;&nbsp;&nbsp;&nbsp;├───geocoding - Геокодирование города \
&nbsp;&nbsp;&nbsp;&nbsp;├───history - История посещений \
&nbsp;&nbsp;&nbsp;&nbsp;├───location - Управление местоположением \
&nbsp;&nbsp;&nbsp;&nbsp;├───preference - Управление предпочтениями \
&nbsp;&nbsp;&nbsp;&nbsp;├───requeststatus - Статусы долгих операций \
&nbsp;&nbsp;&nbsp;&nbsp;└───visit - Список посещения
