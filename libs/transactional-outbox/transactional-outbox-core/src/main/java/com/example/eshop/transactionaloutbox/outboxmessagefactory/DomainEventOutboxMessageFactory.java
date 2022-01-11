package com.example.eshop.transactionaloutbox.outboxmessagefactory;

import com.example.eshop.sharedkernel.domain.base.AggregateRoot;
import com.example.eshop.sharedkernel.domain.base.DomainEvent;
import com.example.eshop.transactionaloutbox.OutboxMessage;
import lombok.RequiredArgsConstructor;
import java.util.Objects;
import java.util.Optional;

/**
 * {@link OutboxMessage} factory for DDD {@link DomainEvent}
 */
@RequiredArgsConstructor
public class DomainEventOutboxMessageFactory {
    private final EventSerializer eventSerializer;
    private final RequestIdSupplier requestIdSupplier;

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
                .type(event.getClass())
                .aggregate(sourceAggregate.getClass())
                .aggregateId(Optional.ofNullable(sourceAggregate.getId()).map(Object::toString).orElse(null))
                .requestId(requestIdSupplier.get())
                .build();
    }
}
