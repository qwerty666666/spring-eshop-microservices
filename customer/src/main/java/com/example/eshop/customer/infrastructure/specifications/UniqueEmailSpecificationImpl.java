package com.example.eshop.customer.infrastructure.specifications;

import com.example.eshop.customer.domain.customer.CustomerRepository;
import com.example.eshop.customer.domain.customer.UniqueEmailSpecification;
import com.example.eshop.sharedkernel.domain.valueobject.Email;
import org.springframework.stereotype.Component;

@Component
public class UniqueEmailSpecificationImpl implements UniqueEmailSpecification {
    private final CustomerRepository customerRepository;

    public UniqueEmailSpecificationImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public boolean isSatisfiedBy(Email email) {
        return !customerRepository.existsByEmail(email);
    }
}
