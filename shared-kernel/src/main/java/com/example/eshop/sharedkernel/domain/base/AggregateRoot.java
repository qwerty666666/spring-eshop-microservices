package com.example.eshop.sharedkernel.domain.base;

/**
 * Aggregate Root in terms of DDD
 */
public interface AggregateRoot<ID extends DomainObjectId> extends Entity<ID> {
}
