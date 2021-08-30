package com.example.eshop.cart.application.usecases.create;

import com.example.eshop.cart.domain.Cart;

public interface CreateCartService {
    /**
     * Creates new {@link Cart} for given customer
     *
     * @throws CartAlreadyExistException if cart already exist for this customer
     */
    void create(String customerId);
}
