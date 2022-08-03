package com.example.eshop.catalog.domain.product;

public class SkuNotFoundException extends RuntimeException {
    public SkuNotFoundException(String message) {
        super(message);
    }
}
