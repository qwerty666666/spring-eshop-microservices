package com.example.eshop.customer.domain.customer;

/**
 * Thrown if {@link Customer} with given Email already registered
 * and therefore this Email can't be used.
 */
public class EmailAlreadyExistException extends RuntimeException {
    public EmailAlreadyExistException(String message) {
        super(message);
    }
}
