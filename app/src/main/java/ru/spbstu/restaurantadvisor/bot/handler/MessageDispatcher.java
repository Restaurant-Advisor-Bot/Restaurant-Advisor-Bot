package ru.spbstu.restaurantadvisor.bot.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.spbstu.restaurantadvisor.bot.dto.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Диспетчер команд
 */
@Component
public class MessageDispatcher {
    
    private static final Logger logger = LoggerFactory.getLogger(MessageDispatcher.class);
    
    // Валидация названия города
    private static final Pattern CITY_NAME_PATTERN = Pattern.compile("^[a-zA-Zа-яА-ЯёЁ\\s-]{2,50}$");
    
    // Максимальная длина текстового запроса
    private static final int MAX_SEARCH_QUERY_LENGTH = 100;
    
    private final Map<String, BotCommandHandler> commandHandlers = new HashMap<>();
    
    public MessageDispatcher(List<BotCommandHandler> handlers) {
        handlers.forEach(handler -> commandHandlers.put(handler.getCommand(), handler));
    }
    
    /**
     * Обработка входящего обновления от Telegram
     */
    public String handleUpdate(TelegramUpdate update) {
        try {
            // Обработка текстовых сообщений
            if (update.message() != null && update.message().text() != null) {
                return handleTextMessage(update.message());
            }
            
            // Обработка геолокации
            if (update.message() != null && update.message().location() != null) {
                return handleLocation(update.message());
            }
            
            // Обработка нажатий кнопок (callback queries)
            if (update.callbackQuery() != null) {
                return handleCallbackQuery(update.callbackQuery());
            }
            
            // Невалидные типы сообщений
            return "Пожалуйста, используйте текстовые команды или отправьте геолокацию. Введите /help для списка доступных команд.";
            
        } catch (Exception e) {
            logger.error("Ошибка при обработке обновления: {}", update, e);
            return "Произошла ошибка при обработке вашего запроса. Попробуйте позже.";
        }
    }
    
    /**
     * Обработка текстовых сообщений
     */
    private String handleTextMessage(TelegramMessage message) {
        String text = message.text().trim();
        long telegramId = message.from().id();
        
        // Проверка, является ли сообщение командой
        if (!text.startsWith("/")) {
            return "Неизвестная команда. Введите /help для списка доступных команд.";
        }
        
        // Парсинг команды и параметров
        String[] parts = text.split("\\s+", 2);
        String command = parts[0].toLowerCase();
        String params = parts.length > 1 ? parts[1] : "";
        
        // Специальная обработка команд с параметрами
        if (command.equals("/location") || command.equals("/preferences") || 
            command.equals("/visit") || command.equals("/search")) {
            return handleComplexCommand(command, params, telegramId);
        }
        
        // Поиск обработчика команды
        BotCommandHandler handler = commandHandlers.get(command);
        if (handler == null) {
            return "Неизвестная команда: " + command + ". Введите /help для списка доступных команд.";
        }
        
        return handler.handle(telegramId, params);
    }
    
    /**
     * Обработка команды с подкомандами и параметрами
     */
    private String handleComplexCommand(String baseCommand, String params, long telegramId) {
        // Парсинг подкоманды
        String[] parts = params.split("\\s+", 2);
        String subCommand = parts.length > 0 ? parts[0].toLowerCase() : "";
        String arguments = parts.length > 1 ? parts[1] : "";
        
        String fullCommand = baseCommand;
        String commandParams = params;
        
        // Обработка команд с подкомандами
        if (baseCommand.equals("/location")) {
            if (subCommand.equals("set") || subCommand.equals("show")) {
                fullCommand = baseCommand + " " + subCommand;
                commandParams = arguments;
            }
        } else if (baseCommand.equals("/preferences")) {
            if (subCommand.equals("set") || subCommand.equals("remove") || subCommand.equals("show")) {
                fullCommand = baseCommand + " " + subCommand;
                commandParams = arguments;
            }
        } else if (baseCommand.equals("/visit")) {
            if (subCommand.equals("list") || subCommand.equals("mark") || subCommand.equals("remove")) {
                fullCommand = baseCommand + " " + subCommand;
                commandParams = arguments;
            }
        }
        
        // Валидация для конкретных команд
        if (fullCommand.equals("/location set") && !commandParams.isEmpty()) {
            if (!CITY_NAME_PATTERN.matcher(commandParams).matches()) {
                return "Укажите корректное название города (только буквы, пробелы и дефис)";
            }
        }
        
        if (baseCommand.equals("/search") && params.length() > MAX_SEARCH_QUERY_LENGTH) {
            return "Запрос слишком длинный. Пожалуйста, сократите запрос до " + MAX_SEARCH_QUERY_LENGTH + " символов.";
        }
        
        // Поиск обработчика
        BotCommandHandler handler = commandHandlers.get(fullCommand);
        if (handler == null) {
            // Возврат подсказки с синтаксисом
            return getSyntaxHelp(baseCommand, subCommand);
        }
        
        return handler.handle(telegramId, commandParams);
    }
    
    /**
     * Подсказка с синтаксисом команды
     */
    private String getSyntaxHelp(String command, String subCommand) {
        return switch (command) {
            case "/location" -> "Использование: /location set <город> или /location show";
            case "/preferences" -> "Использование: /preferences set <текст>, /preferences remove <номер> или /preferences show";
            case "/visit" -> "Использование: /visit list, /visit mark <id> или /visit remove <id>";
            case "/search" -> "Использование: /search <кухня/ключевые слова>";
            default -> "Неизвестная команда. Введите /help для списка доступных команд.";
        };
    }
    
    /**
     * Обработка геолокации
     */
    private String handleLocation(TelegramMessage message) {
        long telegramId = message.from().id();
        TelegramLocation location = message.location();
        
        // Поиск обработчика для геолокации
        BotCommandHandler handler = commandHandlers.get("/location update");
        if (handler != null) {
            // Формат параметров: "latitude,longitude"
            String params = location.latitude() + "," + location.longitude();
            return handler.handle(telegramId, params);
        }
        
        return "Местоположение получено, но обработчик не настроен.";
    }
    
    /**
     * Обработка нажатия кнопок
     */
    private String handleCallbackQuery(TelegramCallbackQuery callbackQuery) {
        long telegramId = callbackQuery.from().id();
        String callbackData = callbackQuery.data();
        
        // Парсинг данных кнопки (формат: "action:param")
        String[] parts = callbackData.split(":", 2);
        String action = parts[0];
        String param = parts.length > 1 ? parts[1] : "";
        
        // Поиск обработчика для callback
        String command = "/callback_" + action;
        BotCommandHandler handler = commandHandlers.get(command);
        
        if (handler != null) {
            return handler.handle(telegramId, param);
        }
        
        return "Действие не поддерживается: " + action;
    }
}
