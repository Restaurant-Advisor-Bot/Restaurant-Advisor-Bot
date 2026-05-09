package ru.spbstu.restaurantadvisor.service.visit;

import ru.spbstu.restaurantadvisor.model.VisitListItem;
import java.util.List;

public interface VisitListService {
    void addToVisitList(long telegramId, String name, String address, String cuisine);
    List<VisitListItem> getVisitList(long telegramId);
    void removeFromVisitList(long telegramId, long entryId);
    void markAsVisited(long telegramId, long entryId);
}