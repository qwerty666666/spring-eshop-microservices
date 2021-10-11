package com.example.eshop.cart.domain.checkout.order;

import com.example.eshop.cart.domain.cart.Cart;
import com.example.eshop.cart.domain.checkout.delivery.DeliveryService;
import com.example.eshop.cart.domain.checkout.payment.PaymentService;
import com.example.eshop.sharedkernel.domain.base.ValueObject;
import lombok.Data;
import org.springframework.lang.Nullable;

/**
 * Value Object representing Order.
 * <p>
 * This VO is used as data holder for intermediate
 * checkout process, and can be in invalid / incomplete state for
 * place order process (it can have unavailable delivery, payment,
 * cart items, and so on).
 */
@Data
public final class Order implements ValueObject {
    private final String customerId;
    private final Cart cart;
    private final DeliveryAddress address;
    @Nullable
    private final DeliveryService deliveryService;
    @Nullable
    private final PaymentService paymentService;
}
