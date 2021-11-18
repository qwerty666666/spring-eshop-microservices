package com.example.eshop.customer.domain.customer;

import lombok.Getter;
import java.util.List;

/**
 * Thrown if given Password does not comply to all Policies.
 */
@Getter
public class PasswordPolicyException extends RuntimeException {
    private final List<String> policyViolationMessages;

    public PasswordPolicyException(List<String> policyViolationMessages) {
        super(String.join(" ", policyViolationMessages));

        this.policyViolationMessages = policyViolationMessages;
    }
}
