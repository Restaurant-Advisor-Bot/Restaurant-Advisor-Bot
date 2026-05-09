package ru.spbstu.restaurantadvisor.service.explanation;

public interface ExplanationService {
    void generateExplanation(long telegramId, String restaurantId, String restaurantName, String preferences, String previousContext);
}