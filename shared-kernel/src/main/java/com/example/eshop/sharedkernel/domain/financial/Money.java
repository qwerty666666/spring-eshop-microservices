package com.example.eshop.sharedkernel.domain.financial;

import lombok.Getter;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

/**
 * Immutable Object representing the Money
 */
@Getter
@Embeddable
public class Money {
    @Column(name = "money_amount", nullable = false)
    private BigDecimal amount;
    @Column(name = "money_currency", nullable = false)
    private Currency currency;

    protected Money() {
    }

    private Money(BigDecimal amount, Currency currency) {
        this.amount = amount;
        this.currency = currency;
    }

    public static Money USD(double amount) {
        return of(amount, "USD");
    }

    /**
     * Creates {@code Money} instance.
     *
     * @param amount amount of money in minor unit of the currency. For example, 2 for Euro, 0 for Yen
     * @param currencyCode ISO-4217 currency code
     * @throws IllegalArgumentException if currencyCode is not a supported ISO-4217 code or null, or amount scale
     * @throws IllegalArgumentException if amount scale exceeds currency scale
     */
    public static Money of(double amount, String currencyCode) {
        var currency = createCurrency(currencyCode);
        var amountBigDecimal = createAmount(amount, currency);

        return new Money(amountBigDecimal, currency);
    }

    /**
     * Returns the {@link Currency} instance for the given currency code.
     *
     * @param currencyCode ISO-4217 currency code
     * @return {@link Currency} instance
     * @throws IllegalArgumentException if currencyCode is not a supported ISO-4217 code or null
     */
    private static Currency createCurrency(String currencyCode) {
        try {
            return Currency.getInstance(currencyCode);
        } catch (Exception e) {
            throw new IllegalArgumentException(String.format("Unknown currency code %s. currencyCode must be"
                    + " in ISO-4217 format", currencyCode));
        }
    }
    /**
     * Creates amount from given double value.
     * <p>
     * The scale of the amount must be compatible with the given currency.
     *
     * @throws IllegalArgumentException if amount scale exceeds currency scale
     */
    private static BigDecimal createAmount(double amount, Currency currency) {
        var bd = BigDecimal.valueOf(amount);

        if (bd.scale() > currency.getDefaultFractionDigits()) {
            throw new IllegalArgumentException("Scale of amount " + amount + " is greater than the scale of the"
                    + "currency " + currency);
        }

        return bd.setScale(currency.getDefaultFractionDigits(), RoundingMode.UNNECESSARY);
    }

    @Override
    public String toString() {
        return amount.toPlainString() + ' ' + currency.getCurrencyCode();
    }
}
