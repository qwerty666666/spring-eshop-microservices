package com.example.eshop.cart.domain.checkout.delivery;

import lombok.Getter;
import java.time.LocalDate;

/**
 * The period for which the shipment can be delivered to
 * the client.
 */
@Getter
public class ShipmentPeriod {
    private final LocalDate from;
    private final LocalDate to;

    public ShipmentPeriod(LocalDate from, LocalDate to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public String toString() {
        return from + " " + to;
    }
}
