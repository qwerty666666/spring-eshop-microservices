package com.example.eshop.cart.domain.checkout.delivery;

import com.example.eshop.sharedkernel.domain.Assertions;
import com.example.eshop.sharedkernel.domain.base.ValueObject;
import java.time.LocalDate;

/**
 * The period for which the shipment can be delivered to
 * the client.
 */
public record ShipmentPeriod(
        LocalDate from,
        LocalDate to
) implements ValueObject {
    public ShipmentPeriod {
        Assertions.notNull(from, "from can't be null");
        Assertions.notNull(to, "to can't be null");

    }

    @Override
    public String toString() {
        return "ShipmentPeriod{ %s - %s }".formatted(from, to);
    }
}
