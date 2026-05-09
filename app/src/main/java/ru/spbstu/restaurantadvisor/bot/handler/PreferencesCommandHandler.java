package ru.spbstu.restaurantadvisor.bot.handler;

import ru.spbstu.restaurantadvisor.service.preference.PreferenceService;
import ru.spbstu.restaurantadvisor.model.Preference;
import ru.spbstu.restaurantadvisor.bot.sender.MessageSender;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PreferencesCommandHandler implements BotCommandHandler {

    private final PreferenceService preferenceService;
    private final MessageSender messageSender;

    public PreferencesCommandHandler(PreferenceService preferenceService,
                                     MessageSender messageSender) {
        this.preferenceService = preferenceService;
        this.messageSender = messageSender;
    }

    @Override
    public String getCommand() {
        return "preferences";
    }

    @Override
    public String handle(long telegramId, String text) {
        if (text.startsWith("set ")) {
            String prefsText = text.substring(4).trim();
            if (prefsText.isEmpty()) {
                return "Использование: /preferences set <ваше предпочтение>";
            }
            try {
                preferenceService.addPreference(telegramId, prefsText);
                List<Preference> list = preferenceService.getPreferences(telegramId);
                return formatPreferences(list);
            } catch (IllegalStateException e) {
                return e.getMessage();
            }
        } else if (text.startsWith("remove ")) {
            try {
                int index = Integer.parseInt(text.substring(7).trim());
                preferenceService.removePreference(telegramId, index);
                return "Предпочтение удалено.";
            } catch (NumberFormatException e) {
                return "Укажите номер предпочтения числом.";
            } catch (IllegalArgumentException | IllegalStateException e) {
                return e.getMessage();
            }
        } else if (text.equals("show")) {
            List<Preference> list = preferenceService.getPreferences(telegramId);
            if (list.isEmpty()) {
                return "У вас пока нет сохраненных предпочтений. Добавьте их с помощью /preferences set <текст>";
            }
            return formatPreferences(list);
        } else {
            return "Неизвестная подкоманда. Доступно: set, remove, show.";
        }
    }

    private String formatPreferences(List<Preference> list) {
        StringBuilder sb = new StringBuilder("Текущие предпочтения:\n");
        for (int i = 0; i < list.size(); i++) {
            sb.append(i + 1).append(". ").append(list.get(i).text()).append("\n");
        }
        return sb.toString();
    }
}