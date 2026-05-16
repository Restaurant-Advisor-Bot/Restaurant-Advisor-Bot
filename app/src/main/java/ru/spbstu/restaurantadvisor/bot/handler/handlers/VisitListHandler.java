package ru.spbstu.restaurantadvisor.bot.handler.handlers;

import org.springframework.stereotype.Component;
import ru.spbstu.restaurantadvisor.bot.handler.BotCommandHandler;
import ru.spbstu.restaurantadvisor.model.VisitListItem;
import ru.spbstu.restaurantadvisor.service.visit.VisitListService;

import java.util.List;

@Component
public class VisitListHandler implements BotCommandHandler {
    
    private final VisitListService visitListService;
    
    public VisitListHandler(VisitListService visitListService) {
        this.visitListService = visitListService;
    }
    
    @Override
    public String getCommand() {
        return "/visit list";
    }

    @Override
    public String handle(long telegramId, String text) {
        List<VisitListItem> visitList = visitListService.getVisitList(telegramId);
        
        if (visitList.isEmpty()) {
            return "Ваш список для посещения пуст.";
        }
        
        StringBuilder response = new StringBuilder("⭐ Ваш список для посещения:\n\n");
        
        for (VisitListItem item : visitList) {
            String status = item.visited() ? "✅ посещен" : "⏳ не посещен";
            response.append(String.format("ID: %d\n", item.id()));
            response.append(String.format("📍 %s\n", item.restaurantName()));
            
            if (item.address() != null && !item.address().isEmpty()) {
                response.append(String.format("   %s\n", item.address()));
            }
            
            if (item.cuisine() != null && !item.cuisine().isEmpty()) {
                response.append(String.format("   Кухня: %s\n", item.cuisine()));
            }
            
            response.append(String.format("   %s\n\n", status));
        }
        
        return response.toString();
    }
}
