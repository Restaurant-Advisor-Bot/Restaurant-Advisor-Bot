package ru.spbstu.restaurantadvisor.service.location;

public interface LocationService {
    void setCity(long telegramId, String city);
    void setCoordinates(long telegramId, double lat, double lon);
    String getLocationDescription(long telegramId);
}