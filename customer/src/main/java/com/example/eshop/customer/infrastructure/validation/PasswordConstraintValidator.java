package com.example.eshop.customer.infrastructure.validation;

import org.passay.PasswordData;
import org.passay.PasswordValidator;
import org.springframework.beans.factory.annotation.Autowired;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword, String> {
    private final PasswordValidator passwordValidator;

    @Autowired
    public PasswordConstraintValidator(PasswordValidator passwordValidator) {
        this.passwordValidator = passwordValidator;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        var result = passwordValidator.validate(new PasswordData(value));

        if (result.isValid()) {
            return true;
        }

        var message = String.join(" ", passwordValidator.getMessages(result));

        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addConstraintViolation();

        return false;
    }
}
