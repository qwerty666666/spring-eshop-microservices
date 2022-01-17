package com.example.eshop.sharedkernel.domain.base;

import org.springframework.data.domain.DomainEvents;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Aggregate Root in terms of DDD.
 * <p>
 * Exposes {@link #registerDomainEvent(DomainEvent)} to subclasses to collect
 * {@link DomainEvent} and expose them via {@link #getDomainEventsAndClear()}.
 * This can be useful when we want to publish all domain events together
 * near transaction boundaries (before or after transaction commit).
 * (We do not use Spring's {@link DomainEvents} because it is used only when
 * repositories save*() or delete*() methods are explicitly called and doesn't
 * used when changed Entities are implicitly flushed on transaction commits).
 */
public abstract class AggregateRoot<ID extends Serializable> implements Entity<ID> {
    private transient List<DomainEvent> domainEvents = new ArrayList<>();

    /**
     * Add {@code event} to list.
     */
    protected void registerDomainEvent(DomainEvent event) {
        domainEvents.add(event);
    }

    /**
     * @return domain events, that were registered in this Aggregate
     */
    public List<DomainEvent> getDomainEventsAndClear() {
        var events = new ArrayList<>(domainEvents);

        clearDomainEvents();

        return events;
    }

    /**
     * Returns domain events for the given type and removes them from list
     */
    public <T extends DomainEvent> List<T> getDomainEventsAndRemove(Class<T> type) {
        var foundEvents = new ArrayList<T>();
        var remainingEvents = new ArrayList<DomainEvent>();

        for (var event: domainEvents) {
            if (type.isAssignableFrom(event.getClass())) {
                foundEvents.add((T) event);
            } else {
                remainingEvents.add(event);
            }
        }

        domainEvents = remainingEvents;

        return foundEvents;
    }

    private void clearDomainEvents() {
        domainEvents.clear();
    }
}
