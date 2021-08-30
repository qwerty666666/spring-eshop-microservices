package com.example.eshop.cart.application.usecases.query;

public class CartNotFoundException extends RuntimeException {
    public CartNotFoundException(String message) {
        super(message);
    }
}
