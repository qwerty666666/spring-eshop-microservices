package com.example.eshop.cart.application.usecases.checkout;

import com.example.eshop.cart.domain.cart.CartMapper;
import com.example.eshop.cart.domain.checkout.order.Order;
import com.example.eshop.sharedkernel.domain.valueobject.Money;
import lombok.Getter;

@Getter
public class Total {
    private final Money cartPrice;
    private final Money deliveryPrice;
    private final Money totalPrice;

    public Total(Order order) {
        var cart = CartMapper.getInstance().toCartDto(order.getCart());

        cartPrice = cart.getTotalPrice();
        deliveryPrice = order.getShipmentInfo() == null ? Money.ZERO : order.getShipmentInfo().price();
        totalPrice = cartPrice.add(deliveryPrice);
    }
}
