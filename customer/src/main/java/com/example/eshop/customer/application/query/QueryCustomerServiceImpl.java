package com.example.eshop.customer.application.query;

import com.example.eshop.customer.application.exceptions.CustomerNotFoundException;
import com.example.eshop.customer.domain.customer.Customer;
import com.example.eshop.customer.domain.customer.CustomerRepository;
import com.example.eshop.sharedkernel.domain.valueobject.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QueryCustomerServiceImpl implements QueryCustomerService {
    private final CustomerRepository customerRepository;

    @Override
    @Transactional(readOnly = true)
    public Customer getByEmail(String email) {
        return customerRepository.findByEmail(Email.fromString(email))
                .orElseThrow(() -> new CustomerNotFoundException("Customer with email " + email + " does not exist"));
    }
}
