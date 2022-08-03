package com.example.eshop.warehouse.client.reservationresult;

import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Error indicating that requested {@code reservingQuantity}
 * can't be reserved, because there is no enough quantity for
 * the given StockItem.
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public final class InsufficientQuantityError extends ReservationError {
    private final int reservingQuantity;
    private final int availableQuantity;

    @JsonCreator
    public InsufficientQuantityError(
            @JsonProperty("ean") Ean ean,
            @JsonProperty("reservingQuantity") int reservingQuantity,
            @JsonProperty("availableQuantity") int availableQuantity,
            @JsonProperty("message") String message) {
        super(message, ean);
        this.reservingQuantity = reservingQuantity;
        this.availableQuantity = availableQuantity;
    }
}
