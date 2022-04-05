package com.example.eshop.warehouse.client.reservationresult;

import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Error for reserving single StockItem
 */
@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS, property = "@type")
public sealed class ReservationError permits StockItemNotFoundError, InsufficientQuantityError {
    protected final String message;
    protected final Ean ean;
}
