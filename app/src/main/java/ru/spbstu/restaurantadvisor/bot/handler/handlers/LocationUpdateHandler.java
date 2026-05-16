package ru.spbstu.restaurantadvisor.bot.handler.handlers;

import org.springframework.stereotype.Component;
import ru.spbstu.restaurantadvisor.bot.handler.BotCommandHandler;
import ru.spbstu.restaurantadvisor.service.location.LocationService;

@Component
public class LocationUpdateHandler implements BotCommandHandler {
    
    private final LocationService locationService;
    
    public LocationUpdateHandler(LocationService locationService) {
        this.locationService = locationService;
    }
    
    @Override
    public String getCommand() {
        return "/location update";
    }

    @Override
    public String handle(long telegramId, String text) {
        // Парсинг координат (формат: "latitude,longitude")
        if (text == null || text.trim().isEmpty()) {
            return "Ошибка обработки геолокации";
        }
        
        try {
            String[] coords = text.split(",");
            if (coords.length != 2) {
                return "Ошибка формата координат";
            }
            
            double latitude = Double.parseDouble(coords[0].trim());
            double longitude = Double.parseDouble(coords[1].trim());
            
            // Установка координат через сервис
            locationService.setCoordinates(telegramId, latitude, longitude);
            
            return "✅ Местоположение обновлено по вашей геопозиции";
        } catch (Exception e) {
            return "❌ Не удалось обработать геолокацию";
        }
    }
}
