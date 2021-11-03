package com.example.eshop.customer.domain.customer;

/**
 * Thrown if given Password does not comply to all Policies.
 */
public class PasswordPolicyException extends RuntimeException {
    public PasswordPolicyException(String message) {
        super(message);
    }
}
