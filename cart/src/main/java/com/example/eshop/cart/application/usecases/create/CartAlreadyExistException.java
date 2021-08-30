package com.example.eshop.cart.application.usecases.create;

public class CartAlreadyExistException extends RuntimeException {
    public CartAlreadyExistException(String message) {
        super(message);
    }
}
