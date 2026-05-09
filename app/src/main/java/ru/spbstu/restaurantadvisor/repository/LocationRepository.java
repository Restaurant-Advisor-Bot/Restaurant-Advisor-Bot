package ru.spbstu.restaurantadvisor.repository;

import ru.spbstu.restaurantadvisor.model.Location;
import java.util.Optional;

public interface LocationRepository {
    void upsert(Location location);
    Optional<Location> findByTelegramId(long telegramId);
}