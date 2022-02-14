package com.example.eshop.cart.stubs;

import com.example.eshop.cart.domain.checkout.order.Order;
import com.example.eshop.cart.domain.checkout.delivery.DeliveryService;
import com.example.eshop.cart.domain.checkout.delivery.ShipmentInfo;
import com.example.eshop.cart.domain.checkout.delivery.ShipmentPeriod;
import com.example.eshop.cart.domain.checkout.delivery.ShipmentNotAvailableException;
import com.example.eshop.sharedkernel.domain.valueobject.Money;
import java.time.LocalDate;

public class DeliveryServiceStub extends DeliveryService {
    public static final Money COST = Money.USD(10);
    private final boolean isSupported;

    public DeliveryServiceStub(boolean isSupported) {
        this.id = new DeliveryServiceId("1");
        this.name = "test delivery";
        this.isSupported = isSupported;
    }

    @Override
    public boolean canDeliver(Order order) {
        return isSupported;
    }

    @Override
    public ShipmentInfo getShipmentInfo(Order order) {
        if (!isSupported) {
            throw new ShipmentNotAvailableException();
        }

        var period = new ShipmentPeriod(LocalDate.now().plusDays(1), LocalDate.now().plusDays(2));
        return new ShipmentInfo(order.getAddress(), period, COST);
    }
}
