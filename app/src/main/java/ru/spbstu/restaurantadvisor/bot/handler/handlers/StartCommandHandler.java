package ru.spbstu.restaurantadvisor.bot.handler.handlers;

import org.springframework.stereotype.Component;
import ru.spbstu.restaurantadvisor.bot.handler.BotCommandHandler;
import ru.spbstu.restaurantadvisor.service.user.UserService;

@Component
public class StartCommandHandler implements BotCommandHandler {
    
    private final UserService userService;
    
    public StartCommandHandler(UserService userService) {
        this.userService = userService;
    }
    
    @Override
    public String getCommand() {
        return "/start";
    }

    @Override
    public String handle(long telegramId, String text) {
        // Создание или получение пользователя
        userService.getOrCreateUser(telegramId);
        
        return """
            👋 Добро пожаловать в Restaurant Advisor Bot!
            
            Я помогу вам найти идеальные рестораны с учетом ваших предпочтений и местоположения.
            
            Для начала работы:
            1️⃣ Укажите ваше местоположение: /location set <город> или отправьте геолокацию
            2️⃣ Добавьте предпочтения: /preferences set <ваше предпочтение>
            3️⃣ Начните поиск: /search <тип кухни>
            
            Введите /help для просмотра всех доступных команд.
            """;
    }
}
