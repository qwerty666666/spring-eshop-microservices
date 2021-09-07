package com.example.eshop.cart.application.usecases.checkout;

import com.example.eshop.cart.domain.checkout.placeorder.PlaceOrderService;
import com.example.eshop.cart.domain.checkout.delivery.DeliveryService;
import com.example.eshop.cart.domain.checkout.delivery.DeliveryServiceNotFoundException;
import com.example.eshop.cart.domain.checkout.payment.PaymentServiceNotAvailableException;
import com.example.eshop.cart.domain.checkout.payment.PaymentServiceNotFoundException;
import com.example.eshop.cart.domain.checkout.delivery.ShipmentNotAvailableException;
import com.example.eshop.cart.domain.checkout.payment.PaymentService;

/**
 * Checkout Process is a process where customers can choose
 * different options before place order (like discounts /
 * payments / shipments).
 *
 * This class ensures consistency of input data and provides
 * information about available options. But as its intermediate
 * step in create order process, order can be set to invalid
 * for saving state. Real validation is performed by
 * {@link PlaceOrderService}.
 */
public interface CheckoutProcessService {
    /**
     * @throws DeliveryServiceNotFoundException if {@code deliveryServiceId} is not null and {@link DeliveryService}
     *         with {@code deliveryServiceId} does not exist
     * @throws ShipmentNotAvailableException if {@code deliveryServiceId} is not null and {@link DeliveryService}
     *         with {@code deliveryServiceId} can't be applied to this order
     * @throws PaymentServiceNotFoundException if {@code paymentServiceId} is not null and {@link PaymentService}
     *          with {@code paymentServiceId} does not exist
     * @throws PaymentServiceNotAvailableException if {@code paymentServiceId} is not null and {@link PaymentService}
     *         with {@code paymentServiceId} can't be applied to this order
     */
    CheckoutForm process(OrderDto orderDto);
}
