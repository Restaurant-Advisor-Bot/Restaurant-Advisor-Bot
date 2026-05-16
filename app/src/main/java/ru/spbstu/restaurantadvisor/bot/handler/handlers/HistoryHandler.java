package ru.spbstu.restaurantadvisor.bot.handler.handlers;

import org.springframework.stereotype.Component;
import ru.spbstu.restaurantadvisor.bot.handler.BotCommandHandler;
import ru.spbstu.restaurantadvisor.model.HistoryEntry;
import ru.spbstu.restaurantadvisor.service.history.HistoryService;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class HistoryHandler implements BotCommandHandler {
    
    private static final int HISTORY_LIMIT = 50;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    
    private final HistoryService historyService;
    
    public HistoryHandler(HistoryService historyService) {
        this.historyService = historyService;
    }
    
    @Override
    public String getCommand() {
        return "/history";
    }

    @Override
    public String handle(long telegramId, String text) {
        List<HistoryEntry> history = historyService.getHistory(telegramId, HISTORY_LIMIT);
        
        if (history.isEmpty()) {
            return "История посещений пока пуста";
        }
        
        StringBuilder response = new StringBuilder("📖 История посещений:\n\n");
        
        for (HistoryEntry entry : history) {
            response.append(String.format("📍 %s\n", entry.restaurantName()));
            
            if (entry.address() != null && !entry.address().isEmpty()) {
                response.append(String.format("   %s\n", entry.address()));
            }
            
            if (entry.cuisine() != null && !entry.cuisine().isEmpty()) {
                response.append(String.format("   Кухня: %s\n", entry.cuisine()));
            }
            
            response.append(String.format("   🗓 %s\n\n", entry.visitedAt().format(DATE_FORMATTER)));
        }
        
        return response.toString();
    }
}
