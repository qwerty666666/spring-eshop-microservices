package com.example.eshop.customer.config;

import com.example.eshop.customer.domain.customer.HashedPasswordFactory;
import com.example.eshop.customer.infrastructure.factories.HashedPasswordFactoryImpl;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.LengthRule;
import org.passay.PasswordValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration("customer-SecurityConfig")
public class SecurityConfig {
    @Bean
    public PasswordValidator passwordValidator() {
        return new PasswordValidator(
                new LengthRule(6, 255),
                new CharacterRule(EnglishCharacterData.Digit, 1)
        );
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public HashedPasswordFactory hashedPasswordFactory() {
        return new HashedPasswordFactoryImpl(passwordEncoder(), passwordValidator());
    }
}
