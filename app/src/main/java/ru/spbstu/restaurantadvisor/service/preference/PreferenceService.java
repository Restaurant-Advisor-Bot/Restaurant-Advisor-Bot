package ru.spbstu.restaurantadvisor.service.preference;

import ru.spbstu.restaurantadvisor.model.Preference;
import java.util.List;

public interface PreferenceService {
    List<Preference> getPreferences(long telegramId);
    void addPreference(long telegramId, String text);
    void removePreference(long telegramId, int index);
}