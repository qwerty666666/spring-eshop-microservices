package com.example.eshop.warehouse.domain;

import com.example.eshop.sharedkernel.domain.Assertions;
import com.example.eshop.sharedkernel.domain.base.AggregateRoot;
import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import com.example.eshop.warehouse.domain.events.ProductStockChangedEvent;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.NaturalId;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * Available Stock information
 */
@Entity
@Table(name = "stock_items")
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
     * Decreases stock quantity by given amount {@code quantity}
     *
     * @throws InsufficientStockQuantityException if {@code quantity} is greater than existed stock quantity
     */
    public void decrease(StockQuantity quantity) {
        stockQuantity = stockQuantity.subtract(quantity);

        registerDomainEvent(new ProductStockChangedEvent(ean, stockQuantity.toInt()));

        log.info("Stock Quantity is decreased by {}. Remaining quantity is {}.", quantity, stockQuantity);
    }

    /**
     * Increases stock quantity by given amount {@code quantity}
     */
    public void increase(StockQuantity quantity) {
        stockQuantity = stockQuantity.add(quantity);

        registerDomainEvent(new ProductStockChangedEvent(ean, stockQuantity.toInt()));

        log.info("Stock Quantity is increased by {}. New quantity is {}.", quantity, stockQuantity);
    }
}
