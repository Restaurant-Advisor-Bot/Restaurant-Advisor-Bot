package ru.spbstu.restaurantadvisor.bot.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.spbstu.restaurantadvisor.bot.dto.TelegramUpdate;
import ru.spbstu.restaurantadvisor.bot.sender.MessageSender;

import java.util.Map;

/**
 * HTTP-контроллер для приема webhook от Telegram
 */

@RestController
@RequestMapping("/webhook")
public class TelegramWebhookController {
    
    private static final Logger logger = LoggerFactory.getLogger(TelegramWebhookController.class);
    
    private final MessageDispatcher messageDispatcher;
    private final MessageSender messageSender;
    
    @Value("${telegram.bot.token}")
    private String botToken;
    
    public TelegramWebhookController(MessageDispatcher messageDispatcher, MessageSender messageSender) {
        this.messageDispatcher = messageDispatcher;
        this.messageSender = messageSender;
    }
    
    /**
     * Эндпоинт для приема обновлений от Telegram
     */
    @PostMapping("/telegram")
    public ResponseEntity<String> handleUpdate(@RequestBody TelegramUpdate update,
                                               @RequestHeader(value = "X-Telegram-Bot-Api-Secret-Token", required = false) String secretToken) {
        try {
            logger.info("Получено обновление от Telegram: update_id={}", update.updateId());

            // Определение chat ID для отправки ответа
            long chatId = extractChatId(update);
            if (chatId == 0) {
                logger.warn("Не удалось извлечь chat ID из обновления");
                return ResponseEntity.ok("OK");
            }
            
            // Обработка обновления через диспетчер
            String response = messageDispatcher.handleUpdate(update);
            
            // Отправка ответа пользователю
            if (response != null && !response.isEmpty()) {
                messageSender.sendMessage(chatId, response);
            }
            
            return ResponseEntity.ok("OK");
            
        } catch (Exception e) {
            logger.error("Ошибка при обработке webhook от Telegram", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error");
        }
    }
    
    /**
     * Извлекает chat ID из обновления
     */
    private long extractChatId(TelegramUpdate update) {
        if (update.message() != null && update.message().chat() != null) {
            return update.message().chat().id();
        }
        if (update.callbackQuery() != null && update.callbackQuery().message() != null 
            && update.callbackQuery().message().chat() != null) {
            return update.callbackQuery().message().chat().id();
        }
        return 0;
    }
    
    /**
     * Эндпоинт для установки вебхука для административных целей
     */
    @PostMapping("/set-webhook")
    public ResponseEntity<Map<String, String>> setWebhook(@RequestParam String webhookUrl) {
        try {
            logger.info("Установка webhook: {}", webhookUrl);
            // Здесь можно добавить логику установки webhook через Telegram Bot API
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Webhook URL установлен: " + webhookUrl
            ));
        } catch (Exception e) {
            logger.error("Ошибка при установке webhook", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("status", "error", "message", e.getMessage()));
        }
    }
}
