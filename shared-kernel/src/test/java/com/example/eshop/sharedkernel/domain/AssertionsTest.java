package com.example.eshop.sharedkernel.domain;

import org.junit.jupiter.api.Test;
import java.util.Collection;
import java.util.Collections;

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
    void testNotEmptyString() {
        //noinspection ConstantConditions
        assertAll(
                () -> assertThatThrownBy(() -> Assertions.notEmpty((String) null, "err"), "null argument")
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
    void testNotEmptyCollection() {
        //noinspection ConstantConditions
        assertAll(
                () -> assertThatThrownBy(() -> Assertions.notEmpty((Collection<?>) null, "err"), "null argument")
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessage("err"),
                () -> assertThatThrownBy(() -> Assertions.notEmpty(Collections.emptyList(), "err"), "empty list")
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessage("err"),
                () -> assertThatNoException()
                        .as("not empty argument")
                        .isThrownBy(() -> Assertions.notEmpty(Collections.singletonList(1), "err"))
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
