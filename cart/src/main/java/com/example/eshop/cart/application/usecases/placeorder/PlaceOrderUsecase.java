package com.example.eshop.cart.application.usecases.placeorder;

import com.example.eshop.cart.domain.checkout.order.CreateOrderDto;
import com.example.eshop.cart.domain.checkout.order.Order;
import com.example.eshop.sharedkernel.domain.validation.ValidationException;

public interface PlaceOrderUsecase {
    /**
     * Creates new order from given Cart.
     *
     * @throws ValidationException if some fields are invalid
     */
    Order place(CreateOrderDto createOrderDto);
}
