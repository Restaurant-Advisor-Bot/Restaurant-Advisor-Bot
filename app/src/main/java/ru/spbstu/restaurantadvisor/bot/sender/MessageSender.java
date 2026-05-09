package ru.spbstu.restaurantadvisor.bot.sender;

public interface MessageSender {
    void sendMessage(long telegramId, String text);
}