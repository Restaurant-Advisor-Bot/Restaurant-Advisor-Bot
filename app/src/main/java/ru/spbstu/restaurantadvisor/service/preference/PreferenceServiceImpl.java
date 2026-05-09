package ru.spbstu.restaurantadvisor.service.preference;

import ru.spbstu.restaurantadvisor.model.Preference;
import ru.spbstu.restaurantadvisor.repository.PreferenceRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PreferenceServiceImpl implements PreferenceService {

    private static final int MAX_PREFERENCES = 10;
    private final PreferenceRepository preferenceRepository;

    public PreferenceServiceImpl(PreferenceRepository preferenceRepository) {
        this.preferenceRepository = preferenceRepository;
    }

    @Override
    public List<Preference> getPreferences(long telegramId) {
        return preferenceRepository.findByTelegramId(telegramId);
    }

    @Override
    public void addPreference(long telegramId, String text) {
        if (preferenceRepository.countByTelegramId(telegramId) >= MAX_PREFERENCES) {
            throw new IllegalStateException("Достигнут лимит предпочтений (10).");
        }
        preferenceRepository.save(telegramId, text);
    }

    @Override
    public void removePreference(long telegramId, int index) {
        List<Preference> list = preferenceRepository.findByTelegramId(telegramId);
        if (list.isEmpty()) {
            throw new IllegalStateException("У вас нет сохраненных предпочтений.");
        }
        if (index < 1 || index > list.size()) {
            throw new IllegalArgumentException("Предпочтение с таким номером не найдено.");
        }
        Preference pref = list.get(index - 1);
        preferenceRepository.deleteByIdAndTelegramId(pref.id(), telegramId);
    }
}