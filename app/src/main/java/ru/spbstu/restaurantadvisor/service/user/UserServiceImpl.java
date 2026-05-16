package ru.spbstu.restaurantadvisor.service.user;

import org.springframework.stereotype.Service;
import ru.spbstu.restaurantadvisor.model.User;
import ru.spbstu.restaurantadvisor.repository.UserRepository;

import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;
    
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User getOrCreateUser(long telegramId) {
        return userRepository.findByTelegramId(telegramId)
            .orElseGet(() -> {
                User newUser = new User(telegramId, "USER", LocalDateTime.now());
                return userRepository.save(newUser);
            });
    }

    @Override
    public boolean isAdmin(long telegramId) {
        return userRepository.findByTelegramId(telegramId)
            .map(user -> "ADMIN".equals(user.role()))
            .orElse(false);
    }
}
