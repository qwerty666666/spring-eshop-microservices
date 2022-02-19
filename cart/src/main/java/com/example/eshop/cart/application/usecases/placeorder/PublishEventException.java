package com.example.eshop.cart.application.usecases.placeorder;

/**
 * Thrown when we can't publish event to message broker
 */
public class PublishEventException extends RuntimeException {
    public PublishEventException(String message) {
        super(message);
    }

    public PublishEventException(Throwable cause) {
        super(cause);
    }
}
