package com.example.eshop.sharedkernel.domain.valueobject;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.Currency;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
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

    @Test
    void whenAddWithDifferentCurrency_thenThrowIllegalArgumentException() {
        var usd = Money.of(10, "USD");
        var eur = Money.of(10, "EUR");

        assertThatIllegalArgumentException().isThrownBy(() -> usd.add(eur));
    }

    @Test
    void whenAdd_thenReturnSum() {
        var five = Money.USD(5);
        var three = Money.USD(3);
        var eight = Money.USD(8);

        assertThat(five.add(three)).isEqualTo(eight);
    }
}
