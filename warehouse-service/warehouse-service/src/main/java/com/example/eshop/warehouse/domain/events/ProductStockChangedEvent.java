package com.example.eshop.warehouse.domain.events;

import com.example.eshop.sharedkernel.domain.base.DomainEvent;
import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import com.example.eshop.warehouse.domain.StockItem;

/**
 * Domain Even fired when {@link StockItem} with given {@code ean}
 * changed.
 */
public record ProductStockChangedEvent(Ean ean, int newQuantity) implements DomainEvent {
}
