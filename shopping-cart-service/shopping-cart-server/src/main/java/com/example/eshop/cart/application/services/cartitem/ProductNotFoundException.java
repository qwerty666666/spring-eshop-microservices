package com.example.eshop.cart.application.services.cartitem;

import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import lombok.Getter;

/**
 * Thrown when product does not exist in catalog
 */
@Getter
public class ProductNotFoundException extends RuntimeException {
    private final Ean ean;

    public ProductNotFoundException(Ean ean, String message) {
        super(message);
        this.ean = ean;
    }
}
