package com.example.eshop.sharedkernel.domain.base;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class AggregateRootTest {
    @Test
    void givenEvent_whenRegisterDomainEvent_thenItIsShouldExistInDomainEventsList() {
        var aggregateRoot = new FakeAggregateRoot();
        var event = new FakeDomainEvent();
        aggregateRoot.addEvent(event);

        var events = aggregateRoot.getDomainEventsAndClear();

        assertAll(
                () -> assertThat(events).as("size").hasSize(1),
                () -> assertThat(events.get(0)).as("event").isSameAs(event)
        );
    }

    @Test
    void givenAggregateRootWithRegisteredEvent_whenGetDomainEventsAndClear_thenDomainEventsListShouldBeEmpty() {
        var aggregateRoot = new FakeAggregateRoot();
        var event = new FakeDomainEvent();
        aggregateRoot.addEvent(event);

        aggregateRoot.getDomainEventsAndClear();

        assertThat(aggregateRoot.getDomainEventsAndClear()).isEmpty();
    }

    private static class FakeAggregateRoot extends AggregateRoot<Integer> {
        @Override
        public Integer getId() {
            return null;
        }

        public void addEvent(DomainEvent event) {
            registerDomainEvent(event);
        }
    }

    private static class FakeDomainEvent implements DomainEvent {
    }
}