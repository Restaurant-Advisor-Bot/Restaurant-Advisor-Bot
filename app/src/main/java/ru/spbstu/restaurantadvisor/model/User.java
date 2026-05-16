package ru.spbstu.restaurantadvisor.model;

import java.time.LocalDateTime;

public record User(long telegramId, String role, LocalDateTime registeredAt) {}
