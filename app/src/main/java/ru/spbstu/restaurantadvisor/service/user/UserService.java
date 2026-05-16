package ru.spbstu.restaurantadvisor.service.user;

import ru.spbstu.restaurantadvisor.model.User;

public interface UserService {
    User getOrCreateUser(long telegramId);
    boolean isAdmin(long telegramId);
}
