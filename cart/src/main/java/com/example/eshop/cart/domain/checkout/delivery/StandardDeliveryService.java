package com.example.eshop.cart.domain.checkout.delivery;

import com.example.eshop.cart.domain.checkout.order.Order;
import com.example.eshop.sharedkernel.domain.valueobject.Money;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.time.LocalDate;

@Entity
@DiscriminatorValue("STANDARD")
public class StandardDeliveryService extends DeliveryService {
    private static final Money PRICE = Money.USD(3);

    @Override
    public ShipmentInfo getShipmentInfo(Order order) {
        var now = LocalDate.now();
        var period = new ShipmentPeriod(now.plusDays(5), now.plusDays(6));

        return new ShipmentInfo(order.getCart(), order.getAddress(), period, PRICE);
    }
}
