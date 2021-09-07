package com.example.eshop.cart.application.usecases.checkout;

import com.example.eshop.cart.domain.checkout.order.Order;
import com.example.eshop.cart.domain.checkout.delivery.DeliveryService;
import com.example.eshop.cart.domain.checkout.payment.PaymentService;
import lombok.Builder;
import lombok.Getter;
import java.util.List;

/**
 * Form for Checkout process.
 */
@Builder
@Getter
public class CheckoutForm {
    private Order order;
    private List<PaymentService> availablePayments;
    private List<DeliveryService> availableDeliveries;
    private Total total;

    private CheckoutForm(CheckoutFormBuilder builder) {
        order = builder.order;
        availablePayments = builder.availablePayments;
        availableDeliveries = builder.availableDeliveries;
        total = builder.total;
    }

    // override Lombok's @Builder
    public static class CheckoutFormBuilder {
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
            if (total == null) {
                throw new IllegalStateException("total is required");
            }
        }
    }
}
