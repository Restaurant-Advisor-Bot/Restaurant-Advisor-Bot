package ru.spbstu.restaurantadvisor.bot.handler.handlers;

import org.springframework.stereotype.Component;
import ru.spbstu.restaurantadvisor.bot.handler.BotCommandHandler;
import ru.spbstu.restaurantadvisor.model.Preference;
import ru.spbstu.restaurantadvisor.service.preference.PreferenceService;

import java.util.List;

@Component
public class PreferencesRemoveHandler implements BotCommandHandler {
    
    private final PreferenceService preferenceService;
    
    public PreferencesRemoveHandler(PreferenceService preferenceService) {
        this.preferenceService = preferenceService;
    }
    
    @Override
    public String getCommand() {
        return "/preferences remove";
    }

    @Override
    public String handle(long telegramId, String text) {
        // Проверка наличия параметра
        if (text == null || text.trim().isEmpty()) {
            return "Использование: /preferences remove <номер>";
        }
        
        // Проверка наличия предпочтений
        List<Preference> preferences = preferenceService.getPreferences(telegramId);
        if (preferences.isEmpty()) {
            return "У вас нет сохраненных предпочтений для удаления.";
        }
        
        // Парсинг номера
        int index;
        try {
            index = Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            return "Укажите корректный номер предпочтения. Используйте /preferences show для просмотра списка.";
        }
        
        // Проверка валидности индекса (нумерация с 1)
        if (index < 1 || index > preferences.size()) {
            return "Предпочтение с таким номером не найдено. Проверьте список через /preferences show";
        }
        
        // Получение текста удаляемого предпочтения
        String removedText = preferences.get(index - 1).text();
        
        // Удаление предпочтения
        preferenceService.removePreference(telegramId, index);
        
        return String.format("✅ Предпочтение '%s' удалено.", removedText);
    }
}
