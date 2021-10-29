package com.example.eshop.cart.domain.checkout.payment;

import com.example.eshop.cart.domain.checkout.order.Order;
import com.example.eshop.cart.domain.checkout.payment.PaymentService.PaymentServiceId;
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
 * This class represents order Payment method.
 * <p>
 * We use it only for checkout process, so it is simple stub and
 * has no logic.
 */
@Entity
@Table(name = "payments")
public abstract class PaymentService extends AggregateRoot<PaymentServiceId> {
    @EmbeddedId
    protected PaymentServiceId id;

    @Column(name = "name")
    @NotEmpty
    protected String name;

    @Override
    public PaymentServiceId getId() {
        return id;
    }

    /**
     * Checks if the given order can be paid with this Payment Service
     */
    public abstract boolean canPay(Order order);

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        PaymentService other = (PaymentService) o;

        return id != null && Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Embeddable
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class PaymentServiceId extends DomainObjectId {
        public PaymentServiceId(String uuid) {
            super(uuid);
        }
    }
}
