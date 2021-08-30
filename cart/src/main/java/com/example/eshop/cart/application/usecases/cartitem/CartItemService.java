package com.example.eshop.cart.application.usecases.cartitem;

import com.example.eshop.cart.domain.Cart;
import com.example.eshop.cart.domain.CartItem;

public interface CartItemService {
    /**
     * Add {@link CartItem} to Customer's {@link Cart}. If customer's cart already
     * contains given CartItem, then quantity will be changed.
     */
    void upsert(UpsertCartItemCommand command);
}
