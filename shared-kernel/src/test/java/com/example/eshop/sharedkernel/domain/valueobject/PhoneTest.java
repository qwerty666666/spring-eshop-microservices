package com.example.eshop.sharedkernel.domain.valueobject;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class PhoneTest {
    @Test
    void whenCreatedWithInvalidFormat_thenThrowInvalidPhoneFormatException() {
        assertAll(
                () -> assertThatThrownBy(() -> Phone.fromString("89999999999"), "start with 8")
                        .isInstanceOf(InvalidPhoneFormatException.class),
                () -> assertThatThrownBy(() -> Phone.fromString("+71234567"), "bad formed")
                        .isInstanceOf(InvalidPhoneFormatException.class),
                () -> assertThatNoException().as("valid")
                        .isThrownBy(() -> Phone.fromString("+79993334444"))
        );
    }
}
