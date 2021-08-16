package com.example.eshop.customer.application.signup;

import com.example.eshop.customer.domain.customer.Customer;

public interface SignUpService {
    /**
     * Creates new Customer
     *
     * @throws com.example.eshop.customer.domain.customer.EmailAlreadyExistException if customer with given
     *          email already exists
     */
    Customer signUp(SignUpCommand command);
}
