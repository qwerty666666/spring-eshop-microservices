package com.example.eshop.cart.application.services.clearcart;

import com.example.eshop.cart.domain.Cart;

public interface ClearCartService {
    /**
     * Clears {@link Cart} for the given Customer
     */
    void clear(String customerId);
}
