package ru.spbstu.restaurantadvisor.service.requeststatus;

public interface RequestStatusService {
    boolean isGenerationInProgress(long telegramId, String restaurantId);
    void startGeneration(long telegramId, String restaurantId);
    void completeGeneration(long telegramId, String restaurantId, boolean success);
}