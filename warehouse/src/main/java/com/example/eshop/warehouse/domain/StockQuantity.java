package com.example.eshop.warehouse.domain;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.Hibernate;
import javax.persistence.Embeddable;
import java.util.Objects;

/**
 * Immutable Class which represents product quantity in stock.
 */
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class StockQuantity implements Comparable<StockQuantity> {
    /**
     * Max available quantity which can be stored by this class
     */
    private static final int MAX_QUANTITY_THRESHOLD = Integer.MAX_VALUE;

    private int quantity;

    private StockQuantity(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("quantity must be non negative integer");
        }

        this.quantity = quantity;
    }

    /**
     * @throws IllegalArgumentException if quantity is negative
     */
    public static StockQuantity of(int quantity) {
        return new StockQuantity(quantity);
    }

    /**
     * @return sum of {@code this} and {@code toAdd} quantity
     *
     * @throws IllegalArgumentException if sum will exceed Integer limits
     */
    public StockQuantity add(StockQuantity toAdd) {
        if (MAX_QUANTITY_THRESHOLD - toAdd.quantity < quantity) {
            throw new StockQuantityLimitExceedException("Can't set quantity more than " + MAX_QUANTITY_THRESHOLD);
        }

        return new StockQuantity(quantity + toAdd.quantity);
    }

    /**
     * @return difference between {@code this} quantity and {@code toSubtract} quantity
     */
    public StockQuantity subtract(StockQuantity toSubtract) {
        if (this.compareTo(toSubtract) < 0) {
            throw new InsufficientStockQuantityException("Can't subtract quantity greater than existed");
        }

        return new StockQuantity(quantity - toSubtract.quantity);
    }

    @Override
    public int compareTo(StockQuantity o) {
        return quantity - o.quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        StockQuantity stockQuantity1 = (StockQuantity) o;

        return Objects.equals(quantity, stockQuantity1.quantity);
    }

    @Override
    public int hashCode() {
        return quantity;
    }
}
