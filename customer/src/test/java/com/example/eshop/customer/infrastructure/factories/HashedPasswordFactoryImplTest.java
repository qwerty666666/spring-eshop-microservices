package com.example.eshop.customer.infrastructure.factories;

import com.example.eshop.customer.domain.customer.PasswordPolicyException;
import org.junit.jupiter.api.Test;
import org.passay.PasswordValidator;
import org.passay.RuleResult;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

class HashedPasswordFactoryImplTest {
    @Test
    void givenInvalidPassword_whenCreateHashedPassword_thenThrowPasswordPolicyException() {
        // Given
        var failureResult = mock(RuleResult.class);
        when(failureResult.isValid()).thenReturn(false);

        var passwordValidator = mock(PasswordValidator.class);
        when(passwordValidator.validate(any())).thenReturn(failureResult);

        var passwordEncoder = mock(PasswordEncoder.class);

        // When
        var factory = new HashedPasswordFactoryImpl(passwordEncoder, passwordValidator);

        // Then
        assertThatExceptionOfType(PasswordPolicyException.class)
                .isThrownBy(() -> factory.createFromPlainPassword("pass"));
    }

    @Test
    void givenPassword_whenCreateHashedPassword_thenPasswordShouldBeEncoded() {
        // Given
        var plainPassword = "pass";
        var encodedPassword = "encoded";

        var passwordEncoder = mock(PasswordEncoder.class);
        when(passwordEncoder.encode(any())).thenReturn(encodedPassword);

        // When
        var factory = new HashedPasswordFactoryImpl(passwordEncoder, new PasswordValidator());
        var hashedPassword = factory.createFromPlainPassword(plainPassword);

        // Then
        assertThat(hashedPassword.toString()).isEqualTo(encodedPassword);
        verify(passwordEncoder).encode("pass");
    }
}