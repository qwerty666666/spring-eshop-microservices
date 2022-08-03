package com.example.eshop.sharedkernel.domain.base;

/**
 * Specification in terms of DDD
 */
public interface Specification<T> {
    boolean isSatisfiedBy(T arg);
}
