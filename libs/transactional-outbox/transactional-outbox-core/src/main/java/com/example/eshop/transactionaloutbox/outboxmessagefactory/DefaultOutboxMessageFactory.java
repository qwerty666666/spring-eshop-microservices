package com.example.eshop.transactionaloutbox.outboxmessagefactory;

import com.example.eshop.sharedkernel.domain.base.AggregateRoot;
import com.example.eshop.sharedkernel.domain.base.DomainEvent;
import com.example.eshop.transactionaloutbox.OutboxMessage;
import lombok.RequiredArgsConstructor;
import java.util.Optional;

@RequiredArgsConstructor
public class DefaultOutboxMessageFactory implements OutboxMessageFactory {
    private final EventSerializer eventSerializer;
    private final RequestIdSupplier requestIdSupplier;

    @Override
    public OutboxMessage create(String topic, DomainEvent event, AggregateRoot<?> sourceAggregate, String key) {
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
