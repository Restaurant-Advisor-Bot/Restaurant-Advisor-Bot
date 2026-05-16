package ru.spbstu.restaurantadvisor.bot.handler.handlers;

import org.springframework.stereotype.Component;
import ru.spbstu.restaurantadvisor.bot.handler.BotCommandHandler;
import ru.spbstu.restaurantadvisor.model.Preference;
import ru.spbstu.restaurantadvisor.service.preference.PreferenceService;

import java.util.List;

@Component
public class PreferencesSetHandler implements BotCommandHandler {
    
    private static final int MAX_PREFERENCES = 10;
    private final PreferenceService preferenceService;
    
    public PreferencesSetHandler(PreferenceService preferenceService) {
        this.preferenceService = preferenceService;
    }
    
    @Override
    public String getCommand() {
        return "/preferences set";
    }

    @Override
    public String handle(long telegramId, String text) {
        // Проверка наличия текста
        if (text == null || text.trim().isEmpty()) {
            return "Использование: /preferences set <ваше предпочтение>";
        }
        
        // Проверка лимита предпочтений
        List<Preference> currentPreferences = preferenceService.getPreferences(telegramId);
        if (currentPreferences.size() >= MAX_PREFERENCES) {
            return "Достигнут лимит предпочтений (10). Удалите ненужные через /preferences remove <номер>";
        }
        
        // Добавление предпочтения
        preferenceService.addPreference(telegramId, text.trim());
        
        // Получение обновленного списка
        List<Preference> updatedPreferences = preferenceService.getPreferences(telegramId);
        
        StringBuilder response = new StringBuilder("✅ Предпочтения обновлены:\n\n");
        for (int i = 0; i < updatedPreferences.size(); i++) {
            response.append(String.format("%d. %s\n", i + 1, updatedPreferences.get(i).text()));
        }
        
        return response.toString();
    }
}
