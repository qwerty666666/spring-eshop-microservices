package com.example.eshop.warehouse.domain;

/**
 * Thrown when {@link StockQuantity} exceed max available quantity limit.
 */
public class StockQuantityLimitExceedException extends RuntimeException {
    public StockQuantityLimitExceedException(String message) {
        super(message);
    }
}
