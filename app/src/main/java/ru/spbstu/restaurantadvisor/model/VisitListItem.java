package ru.spbstu.restaurantadvisor.model;

public record VisitListItem(long id, long telegramId, String restaurantName, String address, String cuisine, boolean visited) {}