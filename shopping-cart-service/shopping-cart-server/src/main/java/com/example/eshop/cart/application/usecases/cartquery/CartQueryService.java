package com.example.eshop.cart.application.usecases.cartquery;

import com.example.eshop.cart.domain.Cart;

public interface CartQueryService {
    /**
     * @return {@link Cart} for the given customer
     *
     * @throws CartNotFoundException
     */
    Cart getForCustomer(String customerId);
}
