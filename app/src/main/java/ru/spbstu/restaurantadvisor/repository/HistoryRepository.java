package ru.spbstu.restaurantadvisor.repository;

import ru.spbstu.restaurantadvisor.model.HistoryEntry;
import java.util.List;

public interface HistoryRepository {
    HistoryEntry save(HistoryEntry entry);
    List<HistoryEntry> findLastByTelegramId(long telegramId, int limit);
}