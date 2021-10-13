package com.example.eshop.cart.application.usecases.placeorder;

import com.example.eshop.cart.domain.checkout.order.Order;
import com.example.eshop.sharedkernel.domain.base.DomainEvent;

public record OrderPlacedEvent(Order order) implements DomainEvent {
}
