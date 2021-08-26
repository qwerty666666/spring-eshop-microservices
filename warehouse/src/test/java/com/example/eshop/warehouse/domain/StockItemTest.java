package com.example.eshop.warehouse.domain;

import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import com.example.eshop.warehouse.domain.events.ProductStockChangedEvent;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StockItemTest {
    private final static Ean EAN = Ean.fromString("0799439112766");
    private final static StockQuantity TEN = StockQuantity.of(10);
    private final static StockQuantity THREE = StockQuantity.of(3);
    private final static StockQuantity SEVEN = StockQuantity.of(7);

    @Test
    void whenDecrease_thenProductStockChangedEventIsRegistered() {
        var stockItem = new StockItem(EAN, TEN);
        var expectedEvent = new ProductStockChangedEvent(EAN, SEVEN.toInt());

        stockItem.decrease(THREE);

        assertThat(stockItem.getDomainEventsAndClear()).contains(expectedEvent);
    }

    @Test
    void whenIncrease_thenProductStockChangedEventIsRegistered() {
        var stockItem = new StockItem(EAN, SEVEN);
        var expectedEvent = new ProductStockChangedEvent(EAN, TEN.toInt());

        stockItem.increase(THREE);

        assertThat(stockItem.getDomainEventsAndClear()).contains(expectedEvent);
    }
}