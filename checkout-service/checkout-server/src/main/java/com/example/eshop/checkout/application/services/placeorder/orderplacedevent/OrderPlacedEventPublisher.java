package com.example.eshop.checkout.application.services.placeorder.orderplacedevent;

import com.example.eshop.checkout.application.services.PublishEventException;
import com.example.eshop.checkout.client.events.orderplacedevent.OrderPlacedEvent;

public interface OrderPlacedEventPublisher {
    /**
     * Publish {@link OrderPlacedEvent} to message broker.
     *
     * @throws PublishEventException if event is not published
     */
    void publish(OrderPlacedEvent event);
}
