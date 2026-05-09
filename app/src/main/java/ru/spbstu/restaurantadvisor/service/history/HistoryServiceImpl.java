package ru.spbstu.restaurantadvisor.service.history;

import ru.spbstu.restaurantadvisor.model.HistoryEntry;
import ru.spbstu.restaurantadvisor.repository.HistoryRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class HistoryServiceImpl implements HistoryService {

    private static final int MAX_HISTORY = 50;
    private final HistoryRepository repository;

    public HistoryServiceImpl(HistoryRepository repository) {
        this.repository = repository;
    }

    @Override
    public void addEntry(long telegramId, String name, String address,
                         String cuisine, LocalDateTime visitedAt) {
        HistoryEntry entry = new HistoryEntry(0, telegramId, name, address, cuisine, visitedAt);
        repository.save(entry);
    }

    @Override
    public void addExplanationNote(long telegramId, String restaurantId, String note) {
        addEntry(telegramId, "Объяснение для " + restaurantId, "", note, LocalDateTime.now());
    }

    @Override
    public List<HistoryEntry> getHistory(long telegramId, int limit) {
        return repository.findLastByTelegramId(telegramId, limit);
    }
}