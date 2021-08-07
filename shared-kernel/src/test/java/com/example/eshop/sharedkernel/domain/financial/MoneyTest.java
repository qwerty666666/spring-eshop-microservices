package com.example.eshop.sharedkernel.domain.financial;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;

class MoneyTest {
    @Test
    void cantCreateMoneyWithExceededScale() {
        assertThatThrownBy(() -> Money.of(10.123, "USD"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void ofFactoryShouldCreateMoney() {
        double amount = 1.23;
        String usd = "USD";

        Money money = Money.of(amount, usd);

        assertAll(
                () -> assertThat(money.getAmount()).as("Amount").isEqualTo(BigDecimal.valueOf(amount)),
                () -> assertThat(money.getCurrency()).as("Currency").isEqualTo(Currency.getInstance(usd))
        );
    }
}
