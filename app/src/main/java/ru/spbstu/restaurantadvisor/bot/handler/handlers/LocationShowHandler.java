package ru.spbstu.restaurantadvisor.bot.handler.handlers;

import org.springframework.stereotype.Component;
import ru.spbstu.restaurantadvisor.bot.handler.BotCommandHandler;
import ru.spbstu.restaurantadvisor.service.location.LocationService;

@Component
public class LocationShowHandler implements BotCommandHandler {
    
    private final LocationService locationService;
    
    public LocationShowHandler(LocationService locationService) {
        this.locationService = locationService;
    }
    
    @Override
    public String getCommand() {
        return "/location show";
    }

    @Override
    public String handle(long telegramId, String text) {
        String locationDescription = locationService.getLocationDescription(telegramId);
        
        if (locationDescription == null || locationDescription.isEmpty()) {
            return "Местоположение не указано. Используйте /location set <город> или отправьте геолокацию.";
        }
        
        return "📍 Текущее местоположение: " + locationDescription;
    }
}
