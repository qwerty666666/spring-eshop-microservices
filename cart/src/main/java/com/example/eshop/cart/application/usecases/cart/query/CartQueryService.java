package com.example.eshop.cart.application.usecases.cart.query;

import com.example.eshop.cart.application.usecases.cart.query.dto.CartDto;
import com.example.eshop.cart.domain.cart.Cart;

public interface CartQueryService {
    /**
     * @return {@link Cart} for the given customer
     */
    CartDto getForCustomer(String customerId);
}
