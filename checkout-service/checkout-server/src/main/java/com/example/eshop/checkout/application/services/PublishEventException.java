package com.example.eshop.checkout.application.services;

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
