package com.example.eshop.cart.application.usecases.query;

import com.example.eshop.cart.application.usecases.query.dto.CartDto;
import com.example.eshop.cart.domain.Cart;

public interface CartQueryService {
    /**
     * @return {@link Cart} for the given customer
     */
    CartDto getForCustomer(String customerId);
}
