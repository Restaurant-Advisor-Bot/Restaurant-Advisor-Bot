package ru.spbstu.restaurantadvisor.bot.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TelegramChat(
    @JsonProperty("id") long id,
    @JsonProperty("type") String type
) {}
