package com.example.eshop.sharedkernel.domain.valueobject;

import com.example.eshop.sharedkernel.domain.base.ValueObject;
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
public class Money implements ValueObject {
    public static final Money ZERO = Money.USD(0);

    @Column(name = "money_amount", nullable = false)
    private BigDecimal amount;
    @Column(name = "money_currency", nullable = false)
    private Currency currency;

    protected Money() {
    }

    private Money(BigDecimal amount, Currency currency) {
        this.amount = scaleAmount(amount, currency);
        this.currency = currency;
    }

    public static Money USD(double amount) {  // NOSONAR
        return of(amount, "USD");
    }

    /**
     * Creates {@code Money} instance.
     *
     * @param amount amount of money in minor unit of the currency
     * @param currencyCode ISO-4217 currency code
     *
     * @throws IllegalArgumentException if currencyCode is not a supported ISO-4217 code or null, or amount scale
     * @throws IllegalArgumentException if amount scale exceeds currency scale
     */
    public static Money of(double amount, String currencyCode) {
        return new Money(BigDecimal.valueOf(amount), createCurrency(currencyCode));
    }

    /**
     * Creates {@code Money} instance.
     *
     * @param amount amount of money in minor unit of the currency
     * @param currencyCode ISO-4217 currency code
     *
     * @throws IllegalArgumentException if currencyCode is not a supported ISO-4217 code or null, or amount scale
     * @throws IllegalArgumentException if amount scale exceeds currency scale
     */
    public static Money of(BigDecimal amount, String currencyCode) {
        return new Money(amount, createCurrency(currencyCode));
    }

    /**
     * Returns the {@link Currency} instance for the given currency code.
     *
     * @param currencyCode ISO-4217 currency code
     *
     * @return {@link Currency} instance
     *
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
     * Creates amount from given value.
     * <p>
     * The scale of the amount must be compatible with the given currency.
     *
     * @throws IllegalArgumentException if amount scale exceeds currency scale
     */
    private BigDecimal scaleAmount(BigDecimal amount, Currency currency) {
        if (amount.scale() > currency.getDefaultFractionDigits()) {
            throw new IllegalArgumentException("Scale of amount " + amount + " is greater than the scale of the "
                    + "currency " + currency);
        }

        return amount.setScale(currency.getDefaultFractionDigits(), RoundingMode.UNNECESSARY);
    }

    /**
     * @return sum of {@code this} and {@code toAdd} amount
     *
     * @throws IllegalArgumentException if toAdd has different Currency
     */
    public Money add(Money toAdd) {
        if (!currency.equals(toAdd.getCurrency())) {
            throw new IllegalArgumentException("Can't add Money with different Currencies");
        }

        return new Money(amount.add(toAdd.amount), currency);
    }

    /**
     * @return Money whose amount is multiplied by {@code mul}
     */
    public Money multiply(int mul) {
        return new Money(amount.multiply(BigDecimal.valueOf(mul)), currency);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Money other = (Money) o;

        return amount.equals(other.amount) && currency.equals(other.currency);
    }

    @Override
    public int hashCode() {
        return 31 * amount.hashCode() + currency.hashCode();
    }

    @Override
    public String toString() {
        return amount.toPlainString() + ' ' + currency.getCurrencyCode();
    }
}
