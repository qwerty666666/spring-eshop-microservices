package com.example.eshop.sharedkernel.domain.valueobject;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.Currency;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class MoneyTest {
    @Test
    void givenAmountWithExceededScale_whenCreateMoney_thenThrowsIllegalArgumentException() {
        assertThatThrownBy(() -> Money.of(10.123, "USD"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void whenCreateMoney_thenMoneyHasCorrectFields() {
        double amount = 1.23;
        String usd = "USD";

        Money money = Money.of(amount, usd);

        assertAll(
                () -> assertThat(money.getAmount()).as("Amount").isEqualTo(BigDecimal.valueOf(amount)),
                () -> assertThat(money.getCurrency()).as("Currency").isEqualTo(Currency.getInstance(usd))
        );
    }
}
