package com.example.eshop.cart.domain.checkout.delivery;

import com.example.eshop.cart.domain.checkout.order.DeliveryAddress;
import com.example.eshop.sharedkernel.domain.Assertions;
import com.example.eshop.sharedkernel.domain.valueobject.Money;

public record ShipmentInfo(
        DeliveryAddress address,
        ShipmentPeriod period,
        Money price) {
    public ShipmentInfo {
        Assertions.notNull(address, "cart must be not null");
        Assertions.notNull(period, "period must be not null");
        Assertions.notNull(price, "price must be not null");
    }
}
