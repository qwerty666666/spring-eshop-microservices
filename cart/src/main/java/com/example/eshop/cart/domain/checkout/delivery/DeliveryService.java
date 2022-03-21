package com.example.eshop.cart.domain.checkout.delivery;

import com.example.eshop.cart.domain.checkout.delivery.DeliveryService.DeliveryServiceId;
import com.example.eshop.cart.domain.checkout.order.Order;
import com.example.eshop.sharedkernel.domain.Assertions;
import com.example.eshop.sharedkernel.domain.base.AggregateRoot;
import com.example.eshop.sharedkernel.domain.base.DomainObjectId;

/**
 * This class represents order Delivery method.
 * <p>
 * We use it only for checkout process, so it is simple stub and
 * has no logic.
 */
public abstract class DeliveryService extends AggregateRoot<DeliveryServiceId> {
    protected DeliveryServiceId id;
    protected String name;

    protected DeliveryService(DeliveryServiceId id, String name) {
        Assertions.notNull(id, "id is required");
        Assertions.notEmpty(name, "name should be not empty");

        this.id = id;
        this.name = name;
    }

    @Override
    public DeliveryServiceId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    /**
     * @return if the shipment can be delivered by this Delivery Service
     */
    public boolean canDeliver(Order order) {
        try {
            getShipmentInfo(order);
            return true;
        } catch (ShipmentNotAvailableException e) {
            return false;
        }
    }

    /**
     * @return information about shipment if it can be delivered by this Delivery Service.
     *
     * @throws ShipmentNotAvailableException if shipment can't be applied to given order
     */
    public abstract ShipmentInfo getShipmentInfo(Order order);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        var that = (DeliveryService) o;

        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public static class DeliveryServiceId extends DomainObjectId {
        public DeliveryServiceId(String uuid) {
            super(uuid);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            var that = (DeliveryServiceId) o;

            return uuid.equals(that.uuid);
        }

        @Override
        public int hashCode() {
            return uuid.hashCode();
        }
    }
}
