package com.example.eshop.cart.application.services.cartitem;

import com.example.eshop.cart.domain.Cart;
import com.example.eshop.catalog.client.SkuWithProductDto;

public interface AddCartItemRule {
    /**
     * Checks if item can be added to the cart.
     *
     * @throws AddToCartRuleViolationException if rule is violated
     */
    void check(Cart cart, AddCartItemCommand command, SkuWithProductDto sku);
}
