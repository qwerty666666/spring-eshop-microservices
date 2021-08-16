package com.example.eshop.customer.application.query;

import com.example.eshop.customer.application.exceptions.CustomerNotFoundException;
import com.example.eshop.customer.domain.customer.Customer;

public interface QueryCustomerService {
    /**
     * Finds Customer by {@code email}
     *
     * @throws CustomerNotFoundException if customer with given email does not exist
     */
    Customer getByEmail(String email);
}
