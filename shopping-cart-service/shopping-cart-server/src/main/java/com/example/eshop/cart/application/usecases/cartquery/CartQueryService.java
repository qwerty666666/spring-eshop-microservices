package com.example.eshop.cart.application.usecases.cartquery;

import com.example.eshop.cart.domain.Cart;

public interface CartQueryService {
    /**
     * @return {@link Cart} for the given customer. If user has no cart
     *         creates new one and return it.
     */
    Cart getForCustomerOrCreate(String customerId);
}
