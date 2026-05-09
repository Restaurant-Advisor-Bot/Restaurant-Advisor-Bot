package ru.spbstu.restaurantadvisor.service.visit;

import ru.spbstu.restaurantadvisor.model.VisitListItem;
import ru.spbstu.restaurantadvisor.repository.VisitListRepository;
import ru.spbstu.restaurantadvisor.service.history.HistoryService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class VisitListServiceImpl implements VisitListService {

    private static final int MAX_VISIT_LIST = 50;
    private final VisitListRepository repository;
    private final HistoryService historyService;

    public VisitListServiceImpl(VisitListRepository repository,
                                HistoryService historyService) {
        this.repository = repository;
        this.historyService = historyService;
    }

    @Override
    public void addToVisitList(long telegramId, String name, String address, String cuisine) {
        if (repository.countByTelegramId(telegramId) >= MAX_VISIT_LIST) {
            throw new IllegalStateException(
                "Достигнут лимит списка. Удалите ненужные записи или отметьте их как посещенные");
        }
        VisitListItem item = new VisitListItem(0, telegramId, name, address, cuisine, false);
        repository.save(item);
    }

    @Override
    public List<VisitListItem> getVisitList(long telegramId) {
        return repository.findAllByTelegramId(telegramId);
    }

    @Override
    public void removeFromVisitList(long telegramId, long entryId) {
        if (repository.deleteByIdAndTelegramId(entryId, telegramId) == 0) {
            throw new IllegalArgumentException("Запись с таким ID не найдена.");
        }
    }

    @Override
    public void markAsVisited(long telegramId, long entryId) {
        var optional = repository.findByIdAndTelegramId(entryId, telegramId);
        if (optional.isEmpty()) {
            throw new IllegalArgumentException("Запись с таким ID не найдена.");
        }
        VisitListItem item = optional.get();
        historyService.addEntry(telegramId, item.restaurantName(), item.address(),
                                item.cuisine(), LocalDateTime.now());
        repository.deleteByIdAndTelegramId(entryId, telegramId);
    }
}