package com.example.eshop.sharedkernel.domain.base;

import org.springframework.data.domain.DomainEvents;
import org.springframework.lang.Nullable;
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
    @Nullable
    private transient List<DomainEvent> domainEvents;

    private void reinitDomainEvents() {
        domainEvents = new ArrayList<>();
    }

    /**
     * Add {@code event} to list.
     */
    protected void registerDomainEvent(DomainEvent event) {
        if (domainEvents == null) {
            reinitDomainEvents();
        }

        domainEvents.add(event);
    }

    /**
     * @return domain events, that were registered in this Aggregate
     */
    public List<DomainEvent> getDomainEventsAndClear() {
        var events = domainEvents;

        reinitDomainEvents();

        return events;
    }
}
