package com.example.eshop.sharedkernel.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class AssertionsTest {
    @Test
    void testNotNull() {
        //noinspection ConstantConditions,ObviousNullCheck
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
        //noinspection ConstantConditions
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

    @Test
    void testEan() {
        assertAll(
                () -> assertThatThrownBy(() -> Assertions.ean("invalid-13len", "err"), "non-digit ean")
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessage("err"),
                () -> assertThatThrownBy(() -> Assertions.ean("12345678", "err"), "8-characters length")
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessage("err"),
                () -> assertThatNoException()
                        .as("valid ean")
                        .isThrownBy(() -> Assertions.ean("4006381333931", "err"))
        );
    }

    @Test
    void testNonNegative() {
        assertAll(
                () -> assertThatThrownBy(() -> Assertions.nonNegative(-1, "err"), "negative num")
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessage("err"),
                () -> assertThatNoException().as("zero num")
                        .isThrownBy(() -> Assertions.nonNegative(0, "err")),
                () -> assertThatNoException().as("positive num")
                        .isThrownBy(() -> Assertions.nonNegative(10, "err"))
        );
    }

    @Test
    void testPositive() {
        assertAll(
                () -> assertThatThrownBy(() -> Assertions.positive(-1, "err"), "negative num")
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessage("err"),
                () -> assertThatThrownBy(() -> Assertions.positive(0, "err"), "zero num")
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessage("err"),
                () -> assertThatNoException().as("positive num")
                        .isThrownBy(() -> Assertions.positive(1, "err"))
        );
    }
}
