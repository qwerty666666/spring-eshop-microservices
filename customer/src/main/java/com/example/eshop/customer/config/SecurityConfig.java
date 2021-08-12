package com.example.eshop.customer.config;

import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.LengthRule;
import org.passay.PasswordValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration("customerSecurityConfig")
public class SecurityConfig {
    @Bean
    public PasswordValidator passwordValidator() {
        return new PasswordValidator(
                new LengthRule(6),
                new CharacterRule(EnglishCharacterData.Digit, 1)
        );
    }
}
