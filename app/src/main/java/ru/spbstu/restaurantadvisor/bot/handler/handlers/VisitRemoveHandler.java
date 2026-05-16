package ru.spbstu.restaurantadvisor.bot.handler.handlers;

import org.springframework.stereotype.Component;
import ru.spbstu.restaurantadvisor.bot.handler.BotCommandHandler;
import ru.spbstu.restaurantadvisor.service.visit.VisitListService;

@Component
public class VisitRemoveHandler implements BotCommandHandler {
    
    private final VisitListService visitListService;
    
    public VisitRemoveHandler(VisitListService visitListService) {
        this.visitListService = visitListService;
    }
    
    @Override
    public String getCommand() {
        return "/visit remove";
    }

    @Override
    public String handle(long telegramId, String text) {
        // Проверка наличия параметра
        if (text == null || text.trim().isEmpty()) {
            return "Использование: /visit remove <id ресторана>";
        }
        
        // Парсинг ID
        long entryId;
        try {
            entryId = Long.parseLong(text.trim());
        } catch (NumberFormatException e) {
            return "Укажите корректный ID ресторана. Используйте /visit list для просмотра списка.";
        }
        
        try {
            // Удаление из списка
            visitListService.removeFromVisitList(telegramId, entryId);
            return "✅ Ресторан удален из списка.";
        } catch (Exception e) {
            return "Запись с таким ID не найдена.";
        }
    }
}
