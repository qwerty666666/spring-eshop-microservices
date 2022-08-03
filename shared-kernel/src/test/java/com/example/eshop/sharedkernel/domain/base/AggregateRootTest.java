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

    @Test
    void givenAggregateRootWithRegisteredDomainEvents_whenGetDomainEventsAndRemoveByType_thenReturnedOnlyTypeSubclassesEventsAndOtherEventsAreRemained() {
        var event = new FakeDomainEvent();
        var subclassEvent = new SubFakeDomainEvent();
        var event2 = new FakeDomainEvent2();

        var aggregateRoot = new FakeAggregateRoot();
        aggregateRoot.addEvent(event);
        aggregateRoot.addEvent(subclassEvent);
        aggregateRoot.addEvent(event2);

        assertThat(aggregateRoot.getDomainEventsAndRemove(FakeDomainEvent.class))
                .containsOnly(event, subclassEvent);
        assertThat(aggregateRoot.getDomainEventsAndClear()).containsOnly(event2);
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

    private static class SubFakeDomainEvent extends FakeDomainEvent {
    }

    private static class FakeDomainEvent2 implements DomainEvent {
    }
}