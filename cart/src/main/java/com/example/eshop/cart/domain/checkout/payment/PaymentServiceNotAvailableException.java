package com.example.eshop.cart.domain.checkout.payment;

public class PaymentServiceNotAvailableException extends RuntimeException {
    public PaymentServiceNotAvailableException(String message) {
        super(message);
    }
}
