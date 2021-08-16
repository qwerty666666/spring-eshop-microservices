package com.example.eshop.sharedkernel.infrastructure.validation.email;

import com.example.eshop.sharedkernel.domain.Assertions;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class EmailConstraintValidator implements ConstraintValidator<ValidEmail, String> {
    @Override
    public boolean isValid(final String email, final ConstraintValidatorContext context) {
        try {
            Assertions.email(email, "");
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
