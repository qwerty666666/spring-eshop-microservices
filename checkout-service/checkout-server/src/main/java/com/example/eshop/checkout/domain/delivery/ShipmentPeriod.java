package com.example.eshop.checkout.domain.delivery;

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
    private static final ShipmentPeriod NULL_PERIOD = new ShipmentPeriod(LocalDate.of(1900, 1, 1),
            LocalDate.of(1900, 1, 1));

    public ShipmentPeriod {
        Assertions.notNull(from, "from can't be null");
        Assertions.notNull(to, "to can't be null");
    }

    /**
     * @return null period object
     */
    public static ShipmentPeriod nullPeriod() {
        return NULL_PERIOD;
    }

    @Override
    public String toString() {
        return "ShipmentPeriod{ %s - %s }".formatted(from, to);
    }
}
