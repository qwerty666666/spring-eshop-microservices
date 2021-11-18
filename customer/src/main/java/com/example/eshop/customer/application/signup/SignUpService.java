package com.example.eshop.customer.application.signup;

import com.example.eshop.customer.domain.customer.Customer;
import com.example.eshop.customer.domain.customer.EmailAlreadyExistException;
import com.example.eshop.customer.domain.customer.PasswordPolicyException;

public interface SignUpService {
    /**
     * Creates new Customer
     *
     * @throws EmailAlreadyExistException if customer with given email already exists
     * @throws PasswordPolicyException if password is not comply to all policies
     */
    Customer signUp(SignUpCommand command);
}
