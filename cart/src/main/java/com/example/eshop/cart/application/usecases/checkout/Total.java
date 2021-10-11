package com.example.eshop.cart.application.usecases.checkout;

import com.example.eshop.cart.domain.cart.Cart;
import com.example.eshop.cart.domain.cart.CartItem;
import com.example.eshop.cart.domain.checkout.delivery.ShipmentInfo;
import com.example.eshop.sharedkernel.domain.valueobject.Money;
import lombok.Getter;
import org.springframework.lang.Nullable;

@Getter
public class Total {
    private final Money cartPrice;
    private final Money deliveryPrice;
    private final Money totalPrice;

    public Total(Money cartPrice, Money deliveryPrice, Money totalPrice) {
        this.cartPrice = cartPrice;
        this.deliveryPrice = deliveryPrice;
        this.totalPrice = totalPrice;
    }

    public Total(Cart cart, @Nullable ShipmentInfo shipmentInfo) {
        cartPrice = cart.getItems().stream()
                .map(CartItem::getPrice)
                .reduce(Money.ZERO, Money::add);
        deliveryPrice = shipmentInfo == null ? Money.ZERO : shipmentInfo.price();
        totalPrice = cartPrice.add(deliveryPrice);
    }
}
