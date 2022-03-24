package com.example.eshop.checkout.application.services.placeorder;

import com.example.eshop.checkout.application.services.CreateOrderDto;
import com.example.eshop.checkout.domain.order.Order;
import com.example.eshop.sharedkernel.domain.validation.ValidationException;

public interface PlaceOrderService {
    /**
     * Creates new order from given Cart.
     *
     * @throws ValidationException if some fields are invalid
     */
    Order place(CreateOrderDto createOrderDto);
}
