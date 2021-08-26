package com.example.eshop.warehouse.domain.events;

import com.example.eshop.sharedkernel.domain.base.DomainEvent;
import com.example.eshop.sharedkernel.domain.valueobject.Ean;

public record ProductStockChangedEvent(Ean ean, int newQuantity) implements DomainEvent {
}
