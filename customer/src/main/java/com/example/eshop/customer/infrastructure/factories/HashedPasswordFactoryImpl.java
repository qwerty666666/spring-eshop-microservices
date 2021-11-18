package com.example.eshop.customer.infrastructure.factories;

import com.example.eshop.customer.domain.customer.HashedPassword;
import com.example.eshop.customer.domain.customer.HashedPasswordFactory;
import com.example.eshop.customer.domain.customer.PasswordPolicyException;
import lombok.RequiredArgsConstructor;
import org.passay.PasswordData;
import org.passay.PasswordValidator;
import org.springframework.security.crypto.password.PasswordEncoder;

@RequiredArgsConstructor
public class HashedPasswordFactoryImpl implements HashedPasswordFactory {
    private final PasswordEncoder passwordEncoder;
    private final PasswordValidator passwordValidator;

    @Override
    public HashedPassword createFromPlainPassword(String plain) {
        var validationResult = passwordValidator.validate(new PasswordData(plain));

        if (!validationResult.isValid()) {
            throw new PasswordPolicyException(passwordValidator.getMessages(validationResult));
        }

        return HashedPassword.fromHash(passwordEncoder.encode(plain));
    }
}
