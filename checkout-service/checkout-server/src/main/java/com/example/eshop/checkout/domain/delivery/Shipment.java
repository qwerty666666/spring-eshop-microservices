package com.example.eshop.checkout.domain.delivery;

import com.example.eshop.checkout.domain.order.DeliveryAddress;
import com.example.eshop.checkout.domain.order.Order;
import com.example.eshop.sharedkernel.domain.Assertions;
import com.example.eshop.sharedkernel.domain.base.ValueObject;
import com.example.eshop.sharedkernel.domain.valueobject.Money;

public record Shipment(
        DeliveryAddress address,
        ShipmentPeriod period,
        Money price
) implements ValueObject {
    private static final Shipment NULL_SHIPMENT = new Shipment(DeliveryAddress.nullAddress(),
            ShipmentPeriod.nullPeriod(), Money.ZERO);

    public Shipment {
        Assertions.notNull(address, "cart must be not null");
        Assertions.notNull(period, "period must be not null");
        Assertions.notNull(price, "price must be not null");
    }

    /**
     * @return null shipment object. Null object is used when there are no
     *         shipment is {@link Order}.
     */
    public static Shipment nullShipment() {
        return NULL_SHIPMENT;
    }
}
