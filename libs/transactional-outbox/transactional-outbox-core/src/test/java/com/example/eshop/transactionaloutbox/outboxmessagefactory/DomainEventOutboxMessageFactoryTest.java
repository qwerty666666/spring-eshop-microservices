package com.example.eshop.transactionaloutbox.outboxmessagefactory;

import com.example.eshop.sharedkernel.domain.base.AggregateRoot;
import com.example.eshop.sharedkernel.domain.base.DomainEvent;
import com.example.eshop.sharedkernel.domain.base.DomainObjectId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DomainEventOutboxMessageFactoryTest {
    @Test
    void testMessageIsCreated() {
        // Given
        var serializedEvent = new byte[] { 1, 2, 3 };
        EventSerializer eventSerializer = (event) -> serializedEvent;

        var requestId = "requestId";
        RequestIdSupplier requestIdSupplier = () -> requestId;

        var factory = new DomainEventOutboxMessageFactory(eventSerializer, requestIdSupplier);

        var topic = "topic";
        var key = "key";
        var aggregateId = "id";

        // When
        var message = factory.create(topic, new Event(), new Aggregate(new Id(aggregateId)), key);

        // Then
        assertEquals(topic, message.getTopic());
        assertArrayEquals(serializedEvent, message.getPayload());
        assertEquals(key, message.getKey());
        assertEquals(requestId, message.getRequestId());
        assertEquals(Aggregate.class.getName(), message.getAggregate());
        assertEquals(aggregateId, message.getAggregateId());
        assertEquals(Event.class.getName(), message.getType());
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

        @Override
        public Id getId() {
            return id;
        }
    }
}