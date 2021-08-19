package com.example.eshop.customer.application.signup;

import com.example.eshop.customer.domain.customer.Customer;
import com.example.eshop.customer.domain.customer.EmailAlreadyExistException;

public interface SignUpService {
    /**
     * Creates new Customer
     *
     * @throws EmailAlreadyExistException if customer with given
     *          email already exists
     */
    Customer signUp(SignUpCommand command);
}
