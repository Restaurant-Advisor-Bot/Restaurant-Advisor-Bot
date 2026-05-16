package ru.spbstu.restaurantadvisor.bot.handler.handlers;

import org.springframework.stereotype.Component;
import ru.spbstu.restaurantadvisor.bot.handler.BotCommandHandler;
import ru.spbstu.restaurantadvisor.model.Preference;
import ru.spbstu.restaurantadvisor.service.preference.PreferenceService;

import java.util.List;

@Component
public class PreferencesShowHandler implements BotCommandHandler {
    
    private final PreferenceService preferenceService;
    
    public PreferencesShowHandler(PreferenceService preferenceService) {
        this.preferenceService = preferenceService;
    }
    
    @Override
    public String getCommand() {
        return "/preferences show";
    }

    @Override
    public String handle(long telegramId, String text) {
        List<Preference> preferences = preferenceService.getPreferences(telegramId);
        
        if (preferences.isEmpty()) {
            return "У вас пока нет сохраненных предпочтений. Добавьте их с помощью /preferences set <текст>";
        }
        
        StringBuilder response = new StringBuilder("📋 Ваши предпочтения:\n\n");
        for (int i = 0; i < preferences.size(); i++) {
            response.append(String.format("%d. %s\n", i + 1, preferences.get(i).text()));
        }
        
        return response.toString();
    }
}
