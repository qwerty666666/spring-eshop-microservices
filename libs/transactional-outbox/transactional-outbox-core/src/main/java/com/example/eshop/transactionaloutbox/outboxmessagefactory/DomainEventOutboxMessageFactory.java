package com.example.eshop.transactionaloutbox.outboxmessagefactory;

import com.example.eshop.sharedkernel.domain.base.AggregateRoot;
import com.example.eshop.sharedkernel.domain.base.DomainEvent;
import com.example.eshop.transactionaloutbox.OutboxMessage;
import java.util.Objects;
import java.util.Optional;

/**
 * {@link OutboxMessage} factory for DDD {@link DomainEvent}
 */
public class DomainEventOutboxMessageFactory {
    private final EventSerializer eventSerializer;
    private final RequestIdSupplier requestIdSupplier;
    private final CustomerIdSupplier customerIdSupplier;

    public DomainEventOutboxMessageFactory(EventSerializer eventSerializer) {
        this(eventSerializer, new NullRequestIdSupplier(), new NullCustomerIdSupplier());
    }

    public DomainEventOutboxMessageFactory(EventSerializer eventSerializer, RequestIdSupplier requestIdSupplier, CustomerIdSupplier customerIdSupplier) {
        Objects.requireNonNull(eventSerializer, "eventSerializer is required");
        Objects.requireNonNull(requestIdSupplier, "requestIdSupplier is required");
        Objects.requireNonNull(customerIdSupplier, "customerIdSupplier is required");

        this.eventSerializer = eventSerializer;
        this.requestIdSupplier = requestIdSupplier;
        this.customerIdSupplier = customerIdSupplier;
    }

    public OutboxMessage create(String topic, DomainEvent event, AggregateRoot<?> sourceAggregate) {
        return create(topic, event, sourceAggregate, null);
    }

    public OutboxMessage create(String topic, DomainEvent event, AggregateRoot<?> sourceAggregate, String key) {
        Objects.requireNonNull(topic, "topic is required");
        Objects.requireNonNull(event, "event is required");
        Objects.requireNonNull(sourceAggregate, "sourceAggregate is required");

        return OutboxMessage.builder()
                .topic(topic)
                .payload(eventSerializer.apply(event))
                .key(key)
                .type(event.getClass().getName())
                .aggregate(sourceAggregate.getClass().getName())
                .aggregateId(Optional.ofNullable(sourceAggregate.getId()).map(Object::toString).orElse(null))
                .requestId(requestIdSupplier.get())
                .customerId(customerIdSupplier.get())
                .build();
    }
}
