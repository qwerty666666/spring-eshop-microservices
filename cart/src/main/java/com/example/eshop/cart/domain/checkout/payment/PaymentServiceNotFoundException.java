package com.example.eshop.cart.domain.checkout.payment;

public class PaymentServiceNotFoundException extends RuntimeException {
    public PaymentServiceNotFoundException(String message) {
        super(message);
    }
}
