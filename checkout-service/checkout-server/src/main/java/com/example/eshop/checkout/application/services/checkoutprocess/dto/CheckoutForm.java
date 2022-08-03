package com.example.eshop.checkout.application.services.checkoutprocess.dto;

import com.example.eshop.checkout.domain.order.Order;
import com.example.eshop.checkout.domain.delivery.DeliveryService;
import com.example.eshop.checkout.domain.payment.PaymentService;
import com.example.eshop.sharedkernel.domain.Assertions;
import lombok.Setter;
import lombok.experimental.Accessors;
import java.util.List;

/**
 * Form for Checkout process.
 */
public record CheckoutForm(
        Order order,
        List<PaymentService> availablePayments,
        List<DeliveryService> availableDeliveries,
        Total total
) {
    public CheckoutForm {
        Assertions.notNull(order, "order must be not null");
        Assertions.notNull(availablePayments, "availablePayments must be not null");
        Assertions.notNull(availableDeliveries, "availableDeliveries must be not null");
        Assertions.notNull(total, "total must be not null");
    }

    private CheckoutForm(CheckoutFormBuilder builder) {
        this(builder.order, builder.availablePayments, builder.availableDeliveries, new Total(builder.order));
    }

    public static CheckoutFormBuilder builder() {
        return new CheckoutFormBuilder();
    }

    @Setter
    @Accessors(fluent = true, chain = true)
    public static class CheckoutFormBuilder {
        private Order order;
        private List<PaymentService> availablePayments;
        private List<DeliveryService> availableDeliveries;

        public CheckoutForm build() {
            validate();
            return new CheckoutForm(this);
        }

        private void validate() {
            if (order == null) {
                throw new IllegalStateException("order is required");
            }
            if (availableDeliveries == null) {
                throw new IllegalStateException("availableDeliveries is required");
            }
            if (availablePayments == null) {
                throw new IllegalStateException("availablePayments is required");
            }
        }
    }
}
