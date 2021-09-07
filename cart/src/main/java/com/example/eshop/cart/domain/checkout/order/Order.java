package com.example.eshop.cart.domain.checkout.order;

import com.example.eshop.cart.domain.cart.Cart;
import com.example.eshop.cart.domain.checkout.delivery.DeliveryService;
import com.example.eshop.cart.domain.checkout.payment.PaymentService;
import com.example.eshop.sharedkernel.domain.Assertions;
import com.example.eshop.sharedkernel.domain.valueobject.Money;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.lang.Nullable;

/**
 * Order created from Cart
 */
@Data
@AllArgsConstructor
public class Order {
    private final String customerId;
    private final Cart cart;
    private final DeliveryAddress address;
    @Nullable
    private DeliveryService deliveryService;
    @Nullable
    private PaymentService paymentService;
    private Money deliveryPrice = Money.ZERO;

    Order(String customerId, Cart cart, DeliveryAddress address) {
        Assertions.notEmpty(customerId, "customerId must be on empty");
        Assertions.notNull(cart, "cart must be non null");
        Assertions.notEmpty(cart.getItems(), "cart must be non empty");
        Assertions.notNull(address, "address must be non null");

        this.customerId = customerId;
        this.cart = cart;
        this.address = address;
    }
}
