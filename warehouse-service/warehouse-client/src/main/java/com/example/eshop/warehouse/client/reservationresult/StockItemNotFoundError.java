package com.example.eshop.warehouse.client.reservationresult;

import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Error indicating that StockItem with given EAN does not
 * exist in warehouse.
 */
public final class StockItemNotFoundError extends ReservationError {
    @JsonCreator
    public StockItemNotFoundError(
            @JsonProperty("ean") Ean ean,
            @JsonProperty("message") String message) {
        super(message, ean);
    }
}
