package com.example.eshop.cart.application.usecases.cartitemcrud;

import com.example.eshop.cart.domain.cart.Cart;
import com.example.eshop.cart.domain.cart.CartItem;
import com.example.eshop.cart.domain.cart.CartItemNotFoundException;
import com.example.eshop.catalog.application.product.ProductNotFoundException;

public interface CartItemCrudService {
    /**
     * Add {@link CartItem} to Customer's {@link Cart}. If customer's cart already
     * contains given CartItem, then quantity will be changed.
     *
     * @throws ProductNotFoundException if product with given EAN does not exist
     */
    void add(AddCartItemCommand command);

    /**
     * Removes {@link CartItem} from Customer's {@link Cart}.
     *
     * @throws CartItemNotFoundException if Cart does not contain CartItem with given EAN
     */
    void remove(RemoveCartItemCommand command);
}
