package com.example.eshop.cart.domain.cart;

import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import lombok.Getter;

/**
 * Thrown when {@link CartItem} with given {@code ean}
 * is not exist in {@link Cart}.
 */
@Getter
public class CartItemNotFoundException extends RuntimeException {
    private final Ean ean;

    public CartItemNotFoundException(Ean ean, String message) {
        super(message);
        this.ean = ean;
    }
}
