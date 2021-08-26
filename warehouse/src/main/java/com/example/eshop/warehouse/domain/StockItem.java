package com.example.eshop.warehouse.domain;

import com.example.eshop.sharedkernel.domain.base.AggregateRoot;
import com.example.eshop.sharedkernel.domain.valueobject.Ean;
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

    @Override
    public Long id() {
        return id;
    }

    public Ean getEan() {
        return ean;
    }

    public StockQuantity getStockQuantity() {
        return stockQuantity;
    }

    /**
     * @throws InsufficientStockQuantityException if {@code quantity} is greater than existed stock quantity
     */
    public void decrease(StockQuantity quantity) {
        stockQuantity = stockQuantity.subtract(quantity);

        log.info("Stock Quantity is decreased by {}. Remaining quantity is {}.", quantity, stockQuantity);
    }

    public void increase(StockQuantity quantity) {
        stockQuantity = stockQuantity.add(quantity);

        log.info("Stock Quantity is increased by {}. New quantity is {}.", quantity, stockQuantity);
    }
}
