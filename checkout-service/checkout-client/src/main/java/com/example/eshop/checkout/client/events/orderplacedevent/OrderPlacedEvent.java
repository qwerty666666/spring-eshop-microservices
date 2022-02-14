package com.example.eshop.checkout.client.events.orderplacedevent;

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
