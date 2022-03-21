package com.example.eshop.cart.application.services.cartquery;

import com.example.eshop.cart.domain.Cart;

public interface CartQueryService {
    /**
     * @return {@link Cart} for the given customer. If user has no cart
     *         creates new one and return it.
     */
    Cart getForCustomerOrCreate(String customerId);
}
