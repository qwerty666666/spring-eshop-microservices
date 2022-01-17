package com.example.eshop.cart.application.usecases.placeorder;

/**
 * Thrown when we can't get result for stock reservation service
 */
public class StockReservationException extends RuntimeException {
    public StockReservationException(String message) {
        super(message);
    }

    public StockReservationException(Throwable cause) {
        super(cause);
    }
}
