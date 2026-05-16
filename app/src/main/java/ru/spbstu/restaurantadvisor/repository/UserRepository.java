package ru.spbstu.restaurantadvisor.repository;

import ru.spbstu.restaurantadvisor.model.User;
import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User save(User user);
    Optional<User> findByTelegramId(long telegramId);
    List<User> findAll();
    List<User> findByRole(String role);
    void updateRole(long telegramId, String role);
}
