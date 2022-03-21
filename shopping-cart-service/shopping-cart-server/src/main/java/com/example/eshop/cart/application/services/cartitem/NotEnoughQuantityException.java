package com.example.eshop.cart.application.services.cartitem;

import com.example.eshop.cart.domain.Cart;
import com.example.eshop.cart.domain.CartItem;
import lombok.Getter;

/**
 * Thrown when there are no enough available quantity to
 * place {@link CartItem} to the {@link Cart}
 */
@Getter
public class NotEnoughQuantityException extends RuntimeException {
    private final int availableQuantity;
    private final int requiredQuantity;

    public NotEnoughQuantityException(String message, int availableQuantity, int requiredQuantity) {
        super(message);
        this.availableQuantity = availableQuantity;
        this.requiredQuantity = requiredQuantity;
    }
}
