package com.example.eshop.cart.domain.checkout.order;

import com.example.eshop.cart.client.model.CartDto;
import com.example.eshop.cart.domain.checkout.delivery.DeliveryService;
import com.example.eshop.cart.domain.checkout.delivery.Shipment;
import com.example.eshop.cart.domain.checkout.payment.PaymentService;
import com.example.eshop.sharedkernel.domain.base.ValueObject;
import com.example.eshop.sharedkernel.domain.valueobject.Money;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.lang.Nullable;
import java.util.Optional;
import java.util.UUID;

/**
 * Value Object representing Order.
 * <p>
 * This VO is used as data holder for intermediate
 * checkout process, and can be in invalid / incomplete state for
 * place order process (it can have unavailable delivery, payment,
 * cart items, and so on).
 */
@Getter
@EqualsAndHashCode
public final class Order implements ValueObject {
    private final UUID id;
    private final String customerId;
    private final CartDto cart;
    private final DeliveryAddress address;
    private final Shipment shipment;
    @Nullable
    private final DeliveryService deliveryService;
    @Nullable
    private final PaymentService paymentService;

    public Order(UUID id, String customerId, CartDto cart, DeliveryAddress address, @Nullable DeliveryService deliveryService,
            @Nullable PaymentService paymentService) {
        this.id = id;
        this.customerId = customerId;
        this.cart = cart;
        this.address = address;
        this.deliveryService = deliveryService;
        this.paymentService = paymentService;

        if (deliveryService != null && deliveryService.canDeliver(this)) {
            this.shipment = deliveryService.getShipment(this);
        } else {
            this.shipment = Shipment.nullShipment();
        }
    }

    /**
     * @return total price of the Order
     */
    public Money getTotalPrice() {
        var cartPrice = cart.getTotalPrice();
        var deliveryPrice = shipment.price();

        return cartPrice.add(deliveryPrice);
    }
}
