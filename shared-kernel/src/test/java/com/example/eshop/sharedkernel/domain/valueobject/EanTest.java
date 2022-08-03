package com.example.eshop.sharedkernel.domain.valueobject;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class EanTest {
    @Test
    void givenInvalidEan_whenCreateEan_thenThrowInvalidEanFormatException() {
        assertAll(
                () -> assertThatThrownBy(() -> Ean.fromString("invalid-13len"), "non-digit ean")
                        .isInstanceOf(InvalidEanFormatException.class),
                () -> assertThatThrownBy(() -> Ean.fromString("12345678"), "8-characters length")
                        .isInstanceOf(InvalidEanFormatException.class),
                () -> assertThatNoException().as("valid ean")
                        .isThrownBy(() -> Ean.fromString("4006381333931"))
        );
    }

    @Test
    void givenEan_whenEanToString_thenReturnOriginalEan() {
        String ean = "4006381333931";

        assertThat(Ean.fromString(ean)).hasToString(ean);
    }
}
