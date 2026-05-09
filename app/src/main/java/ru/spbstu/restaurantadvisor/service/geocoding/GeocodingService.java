package ru.spbstu.restaurantadvisor.service.geocoding;

public interface GeocodingService {
    CityCoordinates geocode(String cityName);
    
    public record CityCoordinates(double latitude, double longitude, String cityName) {}
}