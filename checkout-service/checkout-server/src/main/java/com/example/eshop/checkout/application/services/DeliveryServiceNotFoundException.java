package com.example.eshop.checkout.application.services;

public class DeliveryServiceNotFoundException extends RuntimeException {
    public DeliveryServiceNotFoundException(String message) {
        super(message);
    }
}
