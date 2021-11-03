package com.example.eshop.warehouse.domain;

/**
 * Thrown when you try to subtract from {@link StockQuantity}
 * more qty than it has.
 */
public class InsufficientStockQuantityException extends RuntimeException {
    public InsufficientStockQuantityException(String message) {
        super(message);
    }
}
