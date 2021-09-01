package com.example.eshop.cart.application.usecases.cart.create;

public class CartAlreadyExistException extends RuntimeException {
    public CartAlreadyExistException(String message) {
        super(message);
    }
}
