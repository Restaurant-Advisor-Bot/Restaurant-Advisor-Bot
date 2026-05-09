package ru.spbstu.restaurantadvisor.model;

import java.time.LocalDateTime;

public record HistoryEntry(long id, long telegramId, String restaurantName, String address, String cuisine, LocalDateTime visitedAt) {}