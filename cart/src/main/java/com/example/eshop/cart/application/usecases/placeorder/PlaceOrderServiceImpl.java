package com.example.eshop.cart.application.usecases.placeorder;

import com.example.eshop.cart.domain.checkout.order.CreateOrderDto;
import com.example.eshop.cart.domain.checkout.order.Order;
import com.example.eshop.cart.domain.checkout.order.OrderFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PlaceOrderServiceImpl implements PlaceOrderService {
    private final com.example.eshop.cart.domain.checkout.placeorder.PlaceOrderService placeOrderService;
    private final ApplicationEventPublisher eventPublisher;
    private final OrderFactory orderFactory;

    @Override
    @PreAuthorize("#createOrderDto.customerId() == principal.getCustomerId()")
    @Transactional
    public Order place(CreateOrderDto createOrderDto) {
        // create order with customer's cart
        var order = orderFactory.create(createOrderDto);

        // place order
        placeOrderService.place(order);

        // and publish application event
        eventPublisher.publishEvent(new OrderPlacedEvent(order));

        return order;
    }
}
