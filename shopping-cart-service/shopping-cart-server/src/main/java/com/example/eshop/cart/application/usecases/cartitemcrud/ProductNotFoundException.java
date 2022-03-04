package com.example.eshop.cart.application.usecases.cartitemcrud;

/**
 * Thrown when product does not exist in catalog
 */
public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(String message) {
        super(message);
    }
}
