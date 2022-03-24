package com.example.eshop.checkout.domain.delivery;

import com.example.eshop.cart.client.model.CartItemDto;
import com.example.eshop.checkout.domain.order.Order;
import com.example.eshop.sharedkernel.domain.valueobject.Money;
import java.time.LocalDate;

/**
 * {@link DeliveryService} that can be used when there are
 * less than {@link ExpressDeliveryService#MAX_ITEMS_THRESHOLD}
 * items in the Cart.
 */
public class ExpressDeliveryService extends DeliveryService {
    private static final int MAX_ITEMS_THRESHOLD = 5;
    private static final Money PRICE = Money.USD(10);

    public ExpressDeliveryService(DeliveryServiceId id, String name) {
        super(id, name);
    }

    @Override
    public boolean canDeliver(Order order) {
        var cart = order.getCart();
        var totalItemsQuantity = cart.getItems().stream().mapToInt(CartItemDto::getQuantity).sum();

        return totalItemsQuantity <= MAX_ITEMS_THRESHOLD;
    }

    @Override
    public Shipment getShipment(Order order) {
        if (!canDeliver(order)) {
            throw new ShipmentNotAvailableException();
        }

        var now = LocalDate.now();
        var period = new ShipmentPeriod(now.plusDays(1), now.plusDays(2));

        return new Shipment(order.getAddress(), period, PRICE);
    }
}
