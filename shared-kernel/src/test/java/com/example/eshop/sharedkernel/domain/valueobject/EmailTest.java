package com.example.eshop.sharedkernel.domain.valueobject;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class EmailTest {
    @Test
    void givenInvalidEmail_whenCreateEmail_thenThrowsIllegalArgumentException() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> Email.fromString("invalid-email"));
    }

    @Test
    void givenEmail_whenEmailToString_thenReturnOriginalEmail() {
        var email = "rick-sanchez@example.com";

        assertThat(Email.fromString(email)).hasToString(email);
    }
}
