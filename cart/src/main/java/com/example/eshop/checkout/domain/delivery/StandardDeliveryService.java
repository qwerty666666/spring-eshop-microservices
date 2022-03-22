package com.example.eshop.checkout.domain.delivery;

import com.example.eshop.checkout.domain.order.Order;
import com.example.eshop.sharedkernel.domain.valueobject.Money;
import java.time.LocalDate;

/**
 * {@link DeliveryService} which is available for every Order.
 */
public class StandardDeliveryService extends DeliveryService {
    private static final Money PRICE = Money.USD(3);

    public StandardDeliveryService(DeliveryServiceId id, String name) {
        super(id, name);
    }

    @Override
    public Shipment getShipment(Order order) {
        var now = LocalDate.now();
        var period = new ShipmentPeriod(now.plusDays(5), now.plusDays(6));

        return new Shipment(order.getAddress(), period, PRICE);
    }
}
