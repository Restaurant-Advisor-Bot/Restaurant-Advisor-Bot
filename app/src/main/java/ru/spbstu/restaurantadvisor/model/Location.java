package ru.spbstu.restaurantadvisor.model;

public record Location(long telegramId, double latitude, double longitude, String city) {}