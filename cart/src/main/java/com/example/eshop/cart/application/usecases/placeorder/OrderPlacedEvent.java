package com.example.eshop.cart.application.usecases.placeorder;

import com.example.eshop.cart.domain.checkout.order.Order;
import com.example.eshop.sharedkernel.domain.base.DomainEvent;
import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Event fired when new Order is placed
 */
public record OrderPlacedEvent(
        Order order,
        LocalDateTime creationDate,
        Map<Ean, ProductInfo> productsInfo
) implements DomainEvent {
}
