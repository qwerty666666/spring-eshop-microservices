package com.example.eshop.cart.domain.checkout.delivery;

import com.example.eshop.cart.domain.checkout.order.Order;
import com.example.eshop.sharedkernel.domain.valueobject.Money;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.time.LocalDate;

@Entity
@DiscriminatorValue("EXPRESS")
public class ExpressDeliveryService extends DeliveryService {
    private static final int MAX_ITEMS_THRESHOLD = 5;
    private static final Money PRICE = Money.USD(10);

    @Override
    public boolean canDeliver(Order order) {
        return order.getCart().getTotalItemsQuantity() > MAX_ITEMS_THRESHOLD;
    }

    @Override
    public ShipmentInfo getShipmentInfo(Order order) {
        if (!canDeliver(order)) {
            throw new ShipmentNotAvailableException();
        }

        var now = LocalDate.now();
        var period = new ShipmentPeriod(now.plusDays(1), now.plusDays(2));

        return new ShipmentInfo(order.getCart(), order.getAddress(), period, PRICE);
    }
}
