package com.example.eshop.order.application.eventlisteners;

import com.example.eshop.checkout.client.CheckoutApi;
import com.example.eshop.checkout.client.events.orderplacedevent.OrderPlacedEvent;
import com.example.eshop.order.application.services.createorder.CreateOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderPlacedEventListener {
    private final OrderPlacedEventMapper mapper;
    private final CreateOrderService createOrderService;

    @KafkaListener(
            topics = CheckoutApi.ORDER_PLACED_TOPIC,
            containerFactory = "orderPlacedKafkaListenerContainerFactory"
    )
    public void onOrderPlaced(OrderPlacedEvent event) {
        var order = mapper.toOrder(event);

        createOrderService.save(order);
    }
}
