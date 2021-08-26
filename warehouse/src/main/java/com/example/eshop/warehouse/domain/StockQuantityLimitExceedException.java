package com.example.eshop.warehouse.domain;

public class StockQuantityLimitExceedException extends RuntimeException {
    public StockQuantityLimitExceedException(String message) {
        super(message);
    }
}
