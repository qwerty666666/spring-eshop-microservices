package com.example.eshop.sharedkernel.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class AssertionsTest {
    @Test
    void testNotNull() {
        assertAll(
                () -> assertThatThrownBy(() -> Assertions.notNull(null, "err"), "null argument")
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessage("err"),
                () -> assertThatNoException()
                        .as("non-null argument")
                        .isThrownBy(() -> Assertions.notNull(new Object(), "err"))
        );
    }

    @Test
    void testNotEmpty() {
        assertAll(
                () -> assertThatThrownBy(() -> Assertions.notEmpty(null, "err"), "null argument")
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessage("err"),
                () -> assertThatThrownBy(() -> Assertions.notEmpty("", "err"), "empty argument")
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessage("err"),
                () -> assertThatNoException()
                        .as("not empty argument")
                        .isThrownBy(() -> Assertions.notEmpty(" ", "err"))
        );
    }

    @Test
    void testEmail() {
        assertAll(
                () -> assertThatThrownBy(() -> Assertions.email("qwe", "err"), "invalid email")
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessage("err"),
                () -> assertThatNoException()
                        .as("valid email")
                        .isThrownBy(() -> Assertions.notEmpty("qwe@qwe.qwe", "err"))
        );
    }
}