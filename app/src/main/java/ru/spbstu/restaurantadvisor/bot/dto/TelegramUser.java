package ru.spbstu.restaurantadvisor.bot.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TelegramUser(
    @JsonProperty("id") long id,
    @JsonProperty("is_bot") boolean isBot,
    @JsonProperty("first_name") String firstName,
    @JsonProperty("last_name") String lastName,
    @JsonProperty("username") String username
) {}
