package com.example.eshop.cart.domain.cart;

public class CartItemAlreadyExistException extends RuntimeException {
    public CartItemAlreadyExistException(String message) {
        super(message);
    }
}
