package com.example.eshop.customer.infrastructure.factories;

import com.example.eshop.customer.domain.customer.HashedPassword;
import com.example.eshop.customer.domain.customer.HashedPasswordFactory;
import com.example.eshop.customer.domain.customer.PasswordPolicyException;
import org.passay.PasswordData;
import org.passay.PasswordValidator;
import org.springframework.security.crypto.password.PasswordEncoder;

public class HashedPasswordFactoryImpl implements HashedPasswordFactory {
    private final PasswordEncoder passwordEncoder;
    private final PasswordValidator passwordValidator;

    public HashedPasswordFactoryImpl(PasswordEncoder passwordEncoder, PasswordValidator passwordValidator) {
        this.passwordEncoder = passwordEncoder;
        this.passwordValidator = passwordValidator;
    }

    @Override
    public HashedPassword createFromPlainPassword(String plain) {
        var validationResult = passwordValidator.validate(new PasswordData(plain));

        if (!validationResult.isValid()) {
            var message = String.join(" ", passwordValidator.getMessages(validationResult));
            throw new PasswordPolicyException(message);
        }

        return HashedPassword.fromHash(passwordEncoder.encode(plain));
    }
}
