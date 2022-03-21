package com.example.eshop.cart.domain.checkout.order;

public class PaymentServiceNotFoundException extends RuntimeException {
    public PaymentServiceNotFoundException(String message) {
        super(message);
    }
}
