package com.gap.readliness.exception;

public class StockIsNotEnoughException extends RuntimeException{
    public StockIsNotEnoughException(String message) {
        super(message);
    }
}