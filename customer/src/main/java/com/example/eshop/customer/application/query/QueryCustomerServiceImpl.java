package com.example.eshop.customer.application.query;

import com.example.eshop.customer.application.exceptions.CustomerNotFoundException;
import com.example.eshop.customer.domain.customer.Customer;
import com.example.eshop.customer.domain.customer.CustomerRepository;
import com.example.eshop.sharedkernel.domain.valueobject.Email;
import org.springframework.stereotype.Service;

@Service
public class QueryCustomerServiceImpl implements QueryCustomerService {
    private final CustomerRepository customerRepository;

    public QueryCustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public Customer getByEmail(String email) {
        return customerRepository.findByEmail(Email.fromString(email))
                .orElseThrow(() -> new CustomerNotFoundException("Customer with email " + email + " does not exist"));
    }
}
