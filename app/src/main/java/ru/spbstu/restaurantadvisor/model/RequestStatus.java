package ru.spbstu.restaurantadvisor.model;

public record RequestStatus(long id, long telegramId, String requestType, String referenceId, String status) {}