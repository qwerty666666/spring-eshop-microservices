package com.example.eshop.checkout.stubs;

import com.example.eshop.checkout.domain.delivery.Shipment;
import com.example.eshop.checkout.domain.order.Order;
import com.example.eshop.checkout.domain.delivery.DeliveryService;
import com.example.eshop.checkout.domain.delivery.ShipmentPeriod;
import com.example.eshop.checkout.domain.delivery.ShipmentNotAvailableException;
import com.example.eshop.sharedkernel.domain.valueobject.Money;
import java.time.LocalDate;

public class DeliveryServiceStub extends DeliveryService {
    public static final Money COST = Money.USD(10);
    private final boolean isSupported;

    public DeliveryServiceStub(boolean isSupported) {
        super(new DeliveryServiceId("1"), "test delivery");
        this.isSupported = isSupported;
    }

    @Override
    public boolean canDeliver(Order order) {
        return isSupported;
    }

    @Override
    public Shipment getShipment(Order order) {
        if (!isSupported) {
            throw new ShipmentNotAvailableException();
        }

        var period = new ShipmentPeriod(LocalDate.now().plusDays(1), LocalDate.now().plusDays(2));
        return new Shipment(order.getAddress(), period, COST);
    }
}
