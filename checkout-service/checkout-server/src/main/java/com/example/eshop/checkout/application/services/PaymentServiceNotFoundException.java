package com.example.eshop.checkout.application.services;

public class PaymentServiceNotFoundException extends RuntimeException {
    public PaymentServiceNotFoundException(String message) {
        super(message);
    }
}
