package com.example.eshop.cart.domain.checkout.delivery;

import com.example.eshop.cart.domain.checkout.order.Order;
import com.example.eshop.cart.domain.checkout.delivery.DeliveryService.DeliveryServiceId;
import com.example.eshop.sharedkernel.domain.base.AggregateRoot;
import com.example.eshop.sharedkernel.domain.base.DomainObjectId;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hibernate.Hibernate;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import java.util.Objects;

/**
 * This class represents order Delivery method.
 * <p>
 * We use it only for checkout process, so it is simple stub and
 * has no logic.
 */
@Entity
@Table(name = "deliveries")
public abstract class DeliveryService extends AggregateRoot<DeliveryServiceId> {
    @EmbeddedId
    @Column(name = "id", nullable = false)
    protected DeliveryServiceId id;

    @Column(name = "name")
    @NotEmpty
    protected String name;

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
     * @return information about shipment if it can be delivered by this Delivery Service,
     *          or empty Optional otherwise.
     *
     * @throws ShipmentNotAvailableException if shipment can't be applied to given order
     */
    public abstract ShipmentInfo getShipmentInfo(Order order);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        DeliveryService that = (DeliveryService) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Embeddable
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class DeliveryServiceId extends DomainObjectId {
        public DeliveryServiceId(String uuid) {
            super(uuid);
        }
    }
}
