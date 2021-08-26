package com.example.eshop.warehouse.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class StockQuantityTest {
    private final static StockQuantity TEN = StockQuantity.of(10);
    private final static StockQuantity FIVE = StockQuantity.of(5);
    private final static StockQuantity FIFTEEN = StockQuantity.of(15);
    private final static StockQuantity MAX = StockQuantity.of(Integer.MAX_VALUE);

    @Test
    void givenNegativeQuantity_whenCreateProductQuantity_thenThrowsIllegalArgumentException() {
        assertThatIllegalArgumentException().isThrownBy(() -> StockQuantity.of(-1));
    }

    @Test
    void testCompareTo() {
        assertAll(
                () -> assertThat(FIVE.compareTo(TEN)).isNegative(),
                () -> assertThat(TEN.compareTo(FIVE)).isPositive(),
                () -> assertThat(FIVE.compareTo(FIVE)).isZero()
        );
    }

    @Test
    void givenProductQuantity_whenAdd_thenReturnSum() {
        assertThat(TEN.add(FIVE)).isEqualTo(FIFTEEN);
    }

    @Test
    void givenProductQuantity_whenSumExceedLimit_thenThrowStockQuantityLimitExceedException() {
        assertThatThrownBy(() -> TEN.add(MAX))
                .isInstanceOf(StockQuantityLimitExceedException.class);
    }

    @Test
    void givenProductQuantity_whenSubtract_thenReturnDifference() {
        assertThat(FIFTEEN.subtract(FIVE)).isEqualTo(TEN);
    }

    @Test
    void givenProductQuantity_whenDifferenceIdNegative_thenThrowInsufficientStockQuantityException() {
        assertThatThrownBy(() -> FIVE.subtract(TEN))
                .isInstanceOf(InsufficientStockQuantityException.class);
    }
}
