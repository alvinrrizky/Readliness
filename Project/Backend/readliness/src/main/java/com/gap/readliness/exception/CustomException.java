package com.gap.readliness.exception;

public class CustomException extends RuntimeException {
    private String message;

    public CustomException(String message, Throwable cause) {
        super(cause);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}