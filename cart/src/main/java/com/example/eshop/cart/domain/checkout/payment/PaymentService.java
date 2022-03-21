package com.example.eshop.cart.domain.checkout.payment;

import com.example.eshop.cart.domain.checkout.order.Order;
import com.example.eshop.cart.domain.checkout.payment.PaymentService.PaymentServiceId;
import com.example.eshop.sharedkernel.domain.Assertions;
import com.example.eshop.sharedkernel.domain.base.AggregateRoot;
import com.example.eshop.sharedkernel.domain.base.DomainObjectId;

/**
 * This class represents order Payment method.
 * <p>
 * We use it only for checkout process, so it is simple stub and
 * has no logic.
 */
public abstract class PaymentService extends AggregateRoot<PaymentServiceId> {
    protected PaymentServiceId id;
    protected String name;

    protected PaymentService(PaymentServiceId id, String name) {
        Assertions.notNull(id, "id is required");
        Assertions.notEmpty(name, "name should be not empty");

        this.id = id;
        this.name = name;
    }

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
        if (o == null || getClass() != o.getClass()) return false;

        var that = (PaymentService) o;

        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public static class PaymentServiceId extends DomainObjectId {
        public PaymentServiceId(String uuid) {
            super(uuid);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            var that = (PaymentServiceId) o;

            return uuid.equals(that.uuid);
        }

        @Override
        public int hashCode() {
            return uuid.hashCode();
        }
    }
}
