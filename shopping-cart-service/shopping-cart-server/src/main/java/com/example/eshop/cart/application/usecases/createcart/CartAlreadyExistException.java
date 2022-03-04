package com.example.eshop.cart.application.usecases.createcart;

public class CartAlreadyExistException extends RuntimeException {
    public CartAlreadyExistException(String message) {
        super(message);
    }
}
