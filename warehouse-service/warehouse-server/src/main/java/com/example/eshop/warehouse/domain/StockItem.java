package com.example.eshop.warehouse.domain;

import com.example.eshop.sharedkernel.domain.Assertions;
import com.example.eshop.sharedkernel.domain.base.AggregateRoot;
import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import com.example.eshop.warehouse.client.events.ProductStockChangedEvent;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.hibernate.annotations.NaturalId;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * Stock availability information
 */
@Entity
@Table(
        name = "stock_items",
        indexes = @Index(name = "stock_items_ean_idx", columnList = "ean")
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
public class StockItem extends AggregateRoot<Long> {
    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false)
    private Long id;

    @Embedded
    @NaturalId
    @NotNull
    private Ean ean;

    @Embedded
    @NotNull
    private StockQuantity stockQuantity;

    public StockItem(Ean ean, StockQuantity stockQuantity) {
        Assertions.notNull(ean, "EAN must be non null");
        Assertions.notNull(stockQuantity, "Stock Quantity must be non null");

        this.ean = ean;
        this.stockQuantity = stockQuantity;
    }

    @Override
    public Long getId() {
        return id;
    }

    /**
     * @return EAN for this item
     */
    public Ean getEan() {
        return ean;
    }

    /**
     * @return stock quantity for this ean
     */
    public StockQuantity getStockQuantity() {
        return stockQuantity;
    }

    /**
     * @return if given {@code quantity} can be reserved
     */
    public boolean canReserve(StockQuantity quantity) {
        return getStockQuantity().compareTo(quantity) >= 0;
    }

    /**
     * Decreases stock quantity by given amount {@code quantity}
     *
     * @throws InsufficientStockQuantityException if {@code quantity} is greater than existed stock quantity
     */
    public void reserve(StockQuantity quantity) {
        if (!canReserve(quantity)) {
            throw new InsufficientStockQuantityException("Can't reserve" + quantity + "items. Only " +
                    getStockQuantity() + " items are available");
        }

        changeQuantity(stockQuantity.subtract(quantity));
    }

    /**
     * Increases stock quantity by given amount {@code quantity}
     */
    public void supply(StockQuantity quantity) {
        changeQuantity(stockQuantity.add(quantity));
    }

    private void changeQuantity(StockQuantity newQuantity) {
        var oldQuantity = stockQuantity;

        stockQuantity = newQuantity;

        registerDomainEvent(new ProductStockChangedEvent(ean, newQuantity.toInt()));

        log.info("{}: Stock Quantity changed {} -> {}", this, oldQuantity, stockQuantity);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        StockItem stockItem = (StockItem) o;
        return ean != null && Objects.equals(ean, stockItem.ean);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ean);
    }

    @Override
    public String toString() {
        return "StockItem{" + ean + "}";
    }
}
