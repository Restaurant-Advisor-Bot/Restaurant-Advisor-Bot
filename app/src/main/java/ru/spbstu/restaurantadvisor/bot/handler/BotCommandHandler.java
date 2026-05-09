package ru.spbstu.restaurantadvisor.bot.handler;

public interface BotCommandHandler {
    String getCommand();
    String handle(long telegramId, String text); // TODO: Добавить другие параметры по мере необходимости
}