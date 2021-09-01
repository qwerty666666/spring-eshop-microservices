package com.example.eshop.cart.application.usecases.cart.query;

public class CartNotFoundException extends RuntimeException {
    public CartNotFoundException(String message) {
        super(message);
    }
}
