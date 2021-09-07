package com.example.eshop.cart.domain.checkout.delivery;

public class DeliveryServiceNotFoundException extends RuntimeException {
    public DeliveryServiceNotFoundException(String message) {
        super(message);
    }
}
