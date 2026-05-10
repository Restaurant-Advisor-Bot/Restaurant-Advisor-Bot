package ru.spbstu.restaurantadvisor.client;

public class PlacesApiException extends Exception {
    
    public enum ErrorType {
        TIMEOUT,
        QUOTA_EXCEEDED,
        UNKNOWN
    }
    
    private final ErrorType errorType;
    
    public PlacesApiException(ErrorType errorType, String message) {
        super(message);
        this.errorType = errorType;
    }
    
    public ErrorType getErrorType() {
        return errorType;
    }
}