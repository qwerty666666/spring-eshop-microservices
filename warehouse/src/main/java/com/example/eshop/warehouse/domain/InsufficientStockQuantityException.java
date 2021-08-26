package com.example.eshop.warehouse.domain;

public class InsufficientStockQuantityException extends RuntimeException {
    public InsufficientStockQuantityException(String message) {
        super(message);
    }
}
