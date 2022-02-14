package com.example.eshop.cart.application.usecases.placeorder;

import com.example.eshop.checkout.client.order.OrderDto;
import com.example.eshop.sharedkernel.domain.base.DomainEvent;
import java.time.LocalDateTime;

/**
 * Event fired when new Order is placed
 */
public record OrderPlacedEvent(
        OrderDto order,
        LocalDateTime creationDate
) implements DomainEvent {
}
