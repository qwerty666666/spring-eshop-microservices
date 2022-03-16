package com.example.eshop.cart.application.services.cartitem;

import com.example.eshop.cart.domain.Cart;
import com.example.eshop.catalog.client.model.SkuWithProductDto;

/**
 * Checks if there are enough available quantity to add
 */
public class AvailableQuantityRule implements AddCartItemRule {
    @Override
    public void check(Cart cart, AddCartItemCommand command, SkuWithProductDto sku) {
        if (command.quantity() > sku.getQuantity()) {
            throw new AddToCartRuleViolationException(
                new NotEnoughQuantityException("There are no enough available quantity for " + sku.getEan(),
                    sku.getQuantity(), command.quantity())
            );
        }
    }
}
