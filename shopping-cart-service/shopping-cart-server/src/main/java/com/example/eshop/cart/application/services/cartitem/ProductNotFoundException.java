package com.example.eshop.cart.application.services.cartitem;

/**
 * Thrown when product does not exist in catalog
 */
public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(String message) {
        super(message);
    }
}
