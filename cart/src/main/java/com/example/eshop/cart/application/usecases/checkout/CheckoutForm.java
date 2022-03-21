package com.example.eshop.cart.application.usecases.checkout;

import com.example.eshop.cart.domain.checkout.order.Order;
import com.example.eshop.cart.domain.checkout.delivery.DeliveryService;
import com.example.eshop.cart.domain.checkout.payment.PaymentService;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import java.util.List;

/**
 * Form for Checkout process.
 */
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
        total = new Total(order);
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
