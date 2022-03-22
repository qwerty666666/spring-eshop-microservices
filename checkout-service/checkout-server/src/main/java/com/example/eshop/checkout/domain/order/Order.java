package com.example.eshop.checkout.domain.order;

import com.example.eshop.cart.client.model.CartDto;
import com.example.eshop.checkout.domain.delivery.DeliveryService;
import com.example.eshop.checkout.domain.delivery.Shipment;
import com.example.eshop.checkout.domain.payment.PaymentService;
import com.example.eshop.sharedkernel.domain.Assertions;
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
    /**
     * {@link Shipment} for this Order or {@link Shipment#nullShipment()} if
     * {@link Order#deliveryService} is null or this Order can't be shipped.
     */
    private final Shipment shipment;
    /**
     * Total price for this Order (cart price + delivery cost)
     */
    private final Money totalPrice;
    @Nullable
    private final DeliveryService deliveryService;
    @Nullable
    private final PaymentService paymentService;

    public Order(UUID id, String customerId, CartDto cart, DeliveryAddress address, @Nullable DeliveryService deliveryService,
            @Nullable PaymentService paymentService) {
        Assertions.notNull(id, "id is required");
        Assertions.notNull(customerId, "customerId is required");
        Assertions.notNull(cart, "cart is required");
        Assertions.notNull(address, "address is required");

        this.id = id;
        this.customerId = customerId;
        this.cart = cart;
        this.address = address;
        this.deliveryService = deliveryService;
        this.paymentService = paymentService;
        this.shipment = Optional.ofNullable(deliveryService)
                .filter(service -> service.canDeliver(this))
                .map(service -> service.getShipment(this))
                .orElseGet(Shipment::nullShipment);
        this.totalPrice = cart.getTotalPrice().add(shipment.price());
    }
}
