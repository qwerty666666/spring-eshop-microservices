package com.example.eshop.warehouse.client.events;

import com.example.eshop.sharedkernel.domain.base.DomainEvent;
import com.example.eshop.sharedkernel.domain.valueobject.Ean;

/**
 * Domain Even fired when StockItem with given {@code ean}
 * changed.
 */
public record ProductStockChangedEvent(Ean ean, int newQuantity) implements DomainEvent {
}
