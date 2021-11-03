package com.example.eshop.cart.domain.cart;

/**
 * Thrown when {@link CartItem} with EAN already exists in
 * {@link Cart} and therefore you should use existed one
 * instead of creating new one.
 */
public class CartItemAlreadyExistException extends RuntimeException {
    public CartItemAlreadyExistException(String message) {
        super(message);
    }
}
