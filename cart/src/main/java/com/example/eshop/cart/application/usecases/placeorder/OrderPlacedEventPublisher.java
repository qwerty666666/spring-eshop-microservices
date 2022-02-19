package com.example.eshop.cart.application.usecases.placeorder;

import com.example.eshop.checkout.client.events.orderplacedevent.OrderPlacedEvent;

public interface OrderPlacedEventPublisher {
    /**
     * Publish {@link OrderPlacedEvent} to message broker.
     *
     * @throws PublishEventException if event is not published
     */
    void publish(OrderPlacedEvent event);
}
