package com.example.eshop.cart.domain;

public class CartItemAlreadyExistException extends RuntimeException {
    public CartItemAlreadyExistException(String message) {
        super(message);
    }
}
