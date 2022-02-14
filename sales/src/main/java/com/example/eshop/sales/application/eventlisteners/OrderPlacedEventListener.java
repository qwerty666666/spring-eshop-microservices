package com.example.eshop.sales.application.eventlisteners;

import com.example.eshop.checkout.client.events.orderplacedevent.OrderPlacedEvent;
import com.example.eshop.sales.application.services.createorder.CreateOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderPlacedEventListener {
    private final OrderPlacedEventMapper mapper;
    private final CreateOrderService createOrderService;

    @EventListener
    public void onOrderPlaced(OrderPlacedEvent event) {
        var order = mapper.toOrder(event);

        createOrderService.save(order);
    }
}
