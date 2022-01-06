package com.example.eshop.transactionaloutbox.outboxmessagefactory;

import com.example.eshop.sharedkernel.domain.base.AggregateRoot;
import com.example.eshop.sharedkernel.domain.base.DomainEvent;
import com.example.eshop.sharedkernel.domain.base.DomainObjectId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.lang.Nullable;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DefaultOutboxMessageFactoryTest {
    @Test
    void testMessageIsCreated() {
        // Given
        var serializedEvent = new byte[] { 1, 2, 3 };
        EventSerializer eventSerializer = (event) -> serializedEvent;

        var requestId = "requestId";
        RequestIdSupplier requestIdSupplier = () -> requestId;

        var factory = new DefaultOutboxMessageFactory(eventSerializer, requestIdSupplier);

        var topic = "topic";
        var key = "key";
        var aggregateId = "id";

        // When
        var message = factory.create(topic, new Event(), new Aggregate(new Id(aggregateId)), key);

        // Then
        Assertions.assertEquals(topic, message.getTopic());
        Assertions.assertArrayEquals(serializedEvent, message.getPayload());
        Assertions.assertEquals(key, message.getKey());
        Assertions.assertEquals(requestId, message.getRequestId());
        Assertions.assertEquals(Aggregate.class.getName(), message.getAggregate());
        Assertions.assertEquals(aggregateId, message.getAggregateId());
        Assertions.assertEquals(Event.class.getName(), message.getType());
    }

    private static class Event implements DomainEvent {}

    private static class Id extends DomainObjectId {
        protected Id(String uuid) {
            super(uuid);
        }
    }

    private static class Aggregate extends AggregateRoot<Id> {
        private final Id id;

        private Aggregate(Id id) {
            this.id = id;
        }

        @Nullable
        @Override
        public Id getId() {
            return id;
        }
    }
}