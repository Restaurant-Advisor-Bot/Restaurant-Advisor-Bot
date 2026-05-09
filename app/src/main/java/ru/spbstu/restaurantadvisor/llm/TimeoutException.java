package ru.spbstu.restaurantadvisor.llm;

public class TimeoutException extends Exception {
    public TimeoutException(String message) { super(message); }
}