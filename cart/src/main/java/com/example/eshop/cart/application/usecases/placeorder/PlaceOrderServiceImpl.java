package com.example.eshop.cart.application.usecases.placeorder;

import com.example.eshop.cart.domain.checkout.order.CreateOrderDto;
import com.example.eshop.cart.domain.checkout.order.Order;
import com.example.eshop.cart.domain.checkout.order.OrderFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Clock;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PlaceOrderServiceImpl implements PlaceOrderService {
    private final com.example.eshop.cart.domain.checkout.placeorder.PlaceOrderService placeOrderService;
    private final ApplicationEventPublisher eventPublisher;
    private final OrderFactory orderFactory;
    private final Clock clock;
    private final ProductInfoProvider productInfoProvider;

    @Override
    @PreAuthorize("#createOrderDto.customerId() == principal.getCustomerId()")
    @Transactional
    public Order place(CreateOrderDto createOrderDto) {
        var order = createOrder(createOrderDto);

        placeOrderService.place(order);

        publishOrderCreatedEvent(order);

        return order;
    }

    private Order createOrder(CreateOrderDto createOrderDto) {
        return orderFactory.create(createOrderDto);
    }

    private void publishOrderCreatedEvent(Order order) {
        var creationDate = LocalDateTime.now(clock);
        var productInfo = productInfoProvider.getProductsInfo(order.getCart());

        eventPublisher.publishEvent(new OrderPlacedEvent(order, creationDate, productInfo));
    }
}
