package com.example.eshop.transactionaloutbox.outboxmessagefactory;

import com.example.eshop.sharedkernel.domain.base.AggregateRoot;
import com.example.eshop.sharedkernel.domain.base.DomainEvent;
import com.example.eshop.transactionaloutbox.OutboxMessage;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DefaultOutboxMessageFactory implements OutboxMessageFactory {
    private final EventSerializer eventSerializer;
    private final RequestIdSupplier requestIdSupplier;

    @Override
    public OutboxMessage create(String topic, DomainEvent event, AggregateRoot<?> sourceAggregate) {
        return OutboxMessage.builder()
                .topic(topic)
                .payload(eventSerializer.apply(event))
                .type(event.getClass().getName())
                .aggregate(sourceAggregate.getClass().getName())
                .aggregateId(sourceAggregate.getId() == null ? null : sourceAggregate.getId().toString())
                .requestId(requestIdSupplier.get())
                .build();
    }
}
