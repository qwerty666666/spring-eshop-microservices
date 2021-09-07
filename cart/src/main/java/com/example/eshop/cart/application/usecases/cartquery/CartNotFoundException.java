package com.example.eshop.cart.application.usecases.cartquery;

public class CartNotFoundException extends RuntimeException {
    public CartNotFoundException(String message) {
        super(message);
    }
}
