package ru.spbstu.restaurantadvisor.bot.handler.handlers;

import org.springframework.stereotype.Component;
import ru.spbstu.restaurantadvisor.bot.handler.BotCommandHandler;

@Component
public class HelpCommandHandler implements BotCommandHandler {
    
    @Override
    public String getCommand() {
        return "/help";
    }

    @Override
    public String handle(long telegramId, String text) {
        return """
            🤖 Доступные команды Restaurant Advisor Bot:
            
            📋 Предпочтения:
            /preferences set <текст> - добавить предпочтение
            /preferences remove <номер> - удалить предпочтение
            /preferences show - показать все предпочтения
            
            📍 Местоположение:
            /location set <город> - установить город
            /location show - показать текущее местоположение
            Или отправьте геолокацию для автоматической установки
            
            🔍 Поиск:
            /search <запрос> - найти рестораны по запросу
            /random - случайный ресторан рядом
            
            ⭐ Список "Посетить":
            /visit list - показать список
            /visit mark <id> - отметить как посещенный
            /visit remove <id> - удалить из списка
            
            📖 История:
            /history - показать историю посещений
            
            ℹ️ Другое:
            /help - показать это сообщение
            """;
    }
}
