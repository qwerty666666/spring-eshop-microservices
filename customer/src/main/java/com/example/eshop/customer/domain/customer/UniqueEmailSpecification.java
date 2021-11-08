package com.example.eshop.customer.domain.customer;

import com.example.eshop.sharedkernel.domain.base.Specification;
import com.example.eshop.sharedkernel.domain.valueobject.Email;

/**
 * Check if {@link Customer} with given Email already exists.
 */
public interface UniqueEmailSpecification extends Specification<Email> {
}
