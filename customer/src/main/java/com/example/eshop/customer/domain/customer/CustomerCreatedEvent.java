package com.example.eshop.customer.domain.customer;

import com.example.eshop.sharedkernel.domain.base.DomainEvent;

public record CustomerCreatedEvent(String customerId) implements DomainEvent {
}
