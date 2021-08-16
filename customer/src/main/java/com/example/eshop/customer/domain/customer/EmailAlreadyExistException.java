package com.example.eshop.customer.domain.customer;

/**
 * Customer with given Email already registered
 */
public class EmailAlreadyExistException extends RuntimeException {
    public EmailAlreadyExistException(String message) {
        super(message);
    }
}
