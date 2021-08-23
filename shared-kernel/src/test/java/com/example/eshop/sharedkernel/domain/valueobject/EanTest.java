package com.example.eshop.sharedkernel.domain.valueobject;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

class EanTest {
    @Test
    void givenInvalidEan_whenCreateEan_thenThrowIllegalArgumentException() {
        assertThatIllegalArgumentException().isThrownBy(() -> Ean.fromString("123"));
    }

    @Test
    void givenEan_whenEanToString_thenReturnOriginalEan() {
        String ean = "4006381333931";

        assertThat(Ean.fromString(ean).toString()).isEqualTo(ean);
    }
}