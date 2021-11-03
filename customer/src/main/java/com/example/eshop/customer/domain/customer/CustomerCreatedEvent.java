package com.example.eshop.customer.domain.customer;

import com.example.eshop.sharedkernel.domain.base.DomainEvent;

/**
 * Domain Event fired when new Customer is created.
 */
public record CustomerCreatedEvent(String customerId) implements DomainEvent {
}
