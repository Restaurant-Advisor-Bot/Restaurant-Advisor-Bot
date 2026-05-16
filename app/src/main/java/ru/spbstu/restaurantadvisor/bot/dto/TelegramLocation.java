package ru.spbstu.restaurantadvisor.bot.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TelegramLocation(
    @JsonProperty("latitude") double latitude,
    @JsonProperty("longitude") double longitude
) {}
