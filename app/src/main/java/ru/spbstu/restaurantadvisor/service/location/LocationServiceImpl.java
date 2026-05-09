package ru.spbstu.restaurantadvisor.service.location;

import ru.spbstu.restaurantadvisor.model.Location;
import ru.spbstu.restaurantadvisor.repository.LocationRepository;
import ru.spbstu.restaurantadvisor.service.geocoding.GeocodingService;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class LocationServiceImpl implements LocationService {

    private static final Pattern CITY_PATTERN =
            Pattern.compile("^[a-zA-Zа-яА-ЯёЁ\\s-]{2,50}$");
    private final LocationRepository locationRepository;
    private final GeocodingService geocodingService;

    public LocationServiceImpl(LocationRepository locationRepository,
                               GeocodingService geocodingService) {
        this.locationRepository = locationRepository;
        this.geocodingService = geocodingService;
    }

    @Override
    public void setCity(long telegramId, String city) {
        if (!CITY_PATTERN.matcher(city).matches()) {
            throw new IllegalArgumentException(
                "Укажите корректное название города (только буквы, пробелы и дефис)");
        }
        GeocodingService.CityCoordinates coords = geocodingService.geocode(city);
        Location location = new Location(telegramId, coords.latitude(),
                                         coords.longitude(), coords.cityName());
        locationRepository.upsert(location);
    }

    @Override
    public void setCoordinates(long telegramId, double lat, double lon) {
        String city = "ваша геопозиция";
        Location location = new Location(telegramId, lat, lon, city);
        locationRepository.upsert(location);
    }

    @Override
    public String getLocationDescription(long telegramId) {
        Optional<Location> opt = locationRepository.findByTelegramId(telegramId);
        if (opt.isEmpty()) {
            return "Местоположение не указано";
        }
        Location loc = opt.get();
        if (loc.city() != null && !loc.city().isBlank()) {
            return loc.city() + " (центр города)";
        }
        return "%.6f, %.6f".formatted(loc.latitude(), loc.longitude());
    }
}