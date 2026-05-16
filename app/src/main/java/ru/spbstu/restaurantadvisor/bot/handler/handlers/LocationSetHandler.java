package ru.spbstu.restaurantadvisor.bot.handler.handlers;

import org.springframework.stereotype.Component;
import ru.spbstu.restaurantadvisor.bot.handler.BotCommandHandler;
import ru.spbstu.restaurantadvisor.service.location.LocationService;

@Component
public class LocationSetHandler implements BotCommandHandler {
    
    private final LocationService locationService;
    
    public LocationSetHandler(LocationService locationService) {
        this.locationService = locationService;
    }
    
    @Override
    public String getCommand() {
        return "/location set";
    }

    @Override
    public String handle(long telegramId, String text) {
        // Проверка наличия параметра
        if (text == null || text.trim().isEmpty()) {
            return "Использование: /location set <город>";
        }
        
        String city = text.trim();
        
        try {
            // Установка города через сервис
            locationService.setCity(telegramId, city);
            return String.format("✅ Местоположение сохранено: %s (центр города)", city);
        } catch (Exception e) {
            return "❌ Не удалось установить местоположение. Проверьте название города и попробуйте снова.";
        }
    }
}
