package ru.spbstu.restaurantadvisor.bot.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TelegramUpdate(
    @JsonProperty("update_id") long updateId,
    @JsonProperty("message") TelegramMessage message,
    @JsonProperty("callback_query") TelegramCallbackQuery callbackQuery
) {}
