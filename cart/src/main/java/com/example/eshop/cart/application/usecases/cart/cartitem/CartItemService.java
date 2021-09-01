package com.example.eshop.cart.application.usecases.cart.cartitem;

import com.example.eshop.cart.domain.Cart;
import com.example.eshop.cart.domain.CartItem;
import com.example.eshop.cart.domain.CartItemNotFoundException;

public interface CartItemService {
    /**
     * Add {@link CartItem} to Customer's {@link Cart}. If customer's cart already
     * contains given CartItem, then quantity will be changed.
     */
    void upsert(UpsertCartItemCommand command);

    /**
     * Removes {@link CartItem} from Customer's {@link Cart}.
     *
     * @throws CartItemNotFoundException if Cart does not contain CartItem with given EAN
     */
    void remove(RemoveCartItemCommand command);
}
