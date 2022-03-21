package com.example.eshop.cart.domain.checkout.order;

public class DeliveryServiceNotFoundException extends RuntimeException {
    public DeliveryServiceNotFoundException(String message) {
        super(message);
    }
}
