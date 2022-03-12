package com.example.eshop.cart.application.services.createcart;

import com.example.eshop.cart.domain.Cart;

public interface CreateCartService {
    /**
     * Creates new {@link Cart} for given customer
     *
     * @throws CartAlreadyExistException if cart already exist for this customer
     */
    Cart create(String customerId);
}
