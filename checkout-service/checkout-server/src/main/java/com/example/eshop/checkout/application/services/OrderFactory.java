package com.example.eshop.checkout.application.services;

import com.example.eshop.checkout.domain.order.Order;
import com.example.eshop.sharedkernel.domain.validation.ValidationException;

public interface OrderFactory {
    /**
     * Factory for creating {@link Order}. It ensures that base properties are valid,
     * but can leave {@link Order} in inconsistent (incomplete) state for intermediate
     * Checkout Process.
     *
     * @throws ValidationException if there are some invalid fields in {@code createOrderDto}
     */
    Order create(CreateOrderDto createOrderDto);
}
