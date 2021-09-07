package com.example.eshop.cart.domain.checkout.delivery;

public class ShipmentNotAvailableException extends RuntimeException {
    public ShipmentNotAvailableException() {
    }

    public ShipmentNotAvailableException(String message) {
        super(message);
    }
}
