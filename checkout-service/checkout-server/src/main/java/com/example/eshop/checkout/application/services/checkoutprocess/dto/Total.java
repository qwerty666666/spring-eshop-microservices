package com.example.eshop.checkout.application.services.checkoutprocess.dto;

import com.example.eshop.checkout.domain.order.Order;
import com.example.eshop.sharedkernel.domain.Assertions;
import com.example.eshop.sharedkernel.domain.valueobject.Money;

public record Total(
        Money cartPrice,
        Money deliveryPrice,
        Money totalPrice
) {
    public Total {
        Assertions.notNull(cartPrice, "cartPrice must be not null");
        Assertions.notNull(deliveryPrice, "deliveryPrice must be not null");
        Assertions.notNull(totalPrice, "totalPrice must be not null");
    }

    public Total(Order order) {
        this(
                order.getCart().getTotalPrice(),
                order.getShipment().price(),
                order.getTotalPrice()
        );
    }
}
