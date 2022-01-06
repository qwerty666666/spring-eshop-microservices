package com.example.eshop.transactionaloutbox.outboxmessagefactory;

import com.example.eshop.sharedkernel.domain.base.AggregateRoot;
import com.example.eshop.sharedkernel.domain.base.DomainEvent;
import com.example.eshop.transactionaloutbox.OutboxMessage;

/**
 * {@link OutboxMessage} factory for DDD Domain Events
 */
public interface OutboxMessageFactory {
    default OutboxMessage create(String topic, DomainEvent event, AggregateRoot<?> sourceAggregate) {
        return create(topic, event, sourceAggregate, null);
    }

    OutboxMessage create(String topic, DomainEvent event, AggregateRoot<?> sourceAggregate, String key);
}
