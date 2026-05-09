package ru.spbstu.restaurantadvisor.service.history;

import ru.spbstu.restaurantadvisor.model.HistoryEntry;
import java.time.LocalDateTime;
import java.util.List;

public interface HistoryService {
    void addEntry(long telegramId, String name, String address, String cuisine, LocalDateTime visitedAt);
    void addExplanationNote(long telegramId, String restaurantId, String note);
    List<HistoryEntry> getHistory(long telegramId, int limit);
}