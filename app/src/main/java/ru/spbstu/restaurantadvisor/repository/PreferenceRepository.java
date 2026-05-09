package ru.spbstu.restaurantadvisor.repository;

import ru.spbstu.restaurantadvisor.model.Preference;
import java.util.List;
import java.util.Optional;

public interface PreferenceRepository {
    List<Preference> findByTelegramId(long telegramId);
    Preference save(long telegramId, String text);
    int deleteByIdAndTelegramId(long id, long telegramId);
    Optional<Preference> findByIdAndTelegramId(long id, long telegramId);
    int countByTelegramId(long telegramId);
}