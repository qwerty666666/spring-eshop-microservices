package com.example.eshop.cart.application.usecases.cartquery;

import com.example.eshop.cart.domain.cart.Cart;

public interface CartQueryService {
    /**
     * @return {@link Cart} for the given customer
     */
    Cart getForCustomer(String customerId);
}
