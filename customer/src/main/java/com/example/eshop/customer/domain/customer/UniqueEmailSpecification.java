package com.example.eshop.customer.domain.customer;

import com.example.eshop.sharedkernel.domain.base.Specification;
import com.example.eshop.sharedkernel.domain.valueobject.Email;

/**
 * Customer with Email already exists
 */
public interface UniqueEmailSpecification extends Specification<Email> {
}
