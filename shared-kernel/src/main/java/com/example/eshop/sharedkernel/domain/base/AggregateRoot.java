package com.example.eshop.sharedkernel.domain.base;

import java.io.Serializable;

/**
 * Aggregate Root in terms of DDD
 */
public interface AggregateRoot<ID extends Serializable> extends Entity<ID> {
}
