package com.example.eshop.cart.domain.cart;

import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import lombok.Getter;

@Getter
public class CartItemNotFoundException extends RuntimeException {
    private Ean ean;

    public CartItemNotFoundException(Ean ean, String message) {
        super(message);
        this.ean = ean;
    }
}
