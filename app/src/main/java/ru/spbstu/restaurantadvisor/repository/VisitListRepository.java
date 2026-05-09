package ru.spbstu.restaurantadvisor.repository;

import ru.spbstu.restaurantadvisor.model.VisitListItem;
import java.util.List;
import java.util.Optional;

public interface VisitListRepository {
    VisitListItem save(VisitListItem item);
    Optional<VisitListItem> findByIdAndTelegramId(long id, long telegramId);
    List<VisitListItem> findAllByTelegramId(long telegramId);
    int deleteByIdAndTelegramId(long id, long telegramId);
    int countByTelegramId(long telegramId);
}