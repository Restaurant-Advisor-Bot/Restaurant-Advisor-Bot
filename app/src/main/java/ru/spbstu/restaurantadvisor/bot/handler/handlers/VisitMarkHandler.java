package ru.spbstu.restaurantadvisor.bot.handler.handlers;

import org.springframework.stereotype.Component;
import ru.spbstu.restaurantadvisor.bot.handler.BotCommandHandler;
import ru.spbstu.restaurantadvisor.service.visit.VisitListService;

@Component
public class VisitMarkHandler implements BotCommandHandler {
    
    private final VisitListService visitListService;
    
    public VisitMarkHandler(VisitListService visitListService) {
        this.visitListService = visitListService;
    }
    
    @Override
    public String getCommand() {
        return "/visit mark";
    }

    @Override
    public String handle(long telegramId, String text) {
        // Проверка наличия параметра
        if (text == null || text.trim().isEmpty()) {
            return "Использование: /visit mark <id ресторана>";
        }
        
        // Парсинг ID
        long entryId;
        try {
            entryId = Long.parseLong(text.trim());
        } catch (NumberFormatException e) {
            return "Укажите корректный ID ресторана. Используйте /visit list для просмотра списка.";
        }
        
        try {
            // Отметка как посещенного
            visitListService.markAsVisited(telegramId, entryId);
            return "✅ Ресторан отмечен как посещенный и перемещен в историю.";
        } catch (Exception e) {
            return "Запись с таким ID не найдена. Проверьте список через /visit list.";
        }
    }
}
