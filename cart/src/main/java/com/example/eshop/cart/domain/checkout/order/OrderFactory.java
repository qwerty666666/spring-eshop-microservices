package com.example.eshop.cart.domain.checkout.order;

import com.example.eshop.cart.domain.cart.Cart;
import com.example.eshop.cart.domain.checkout.placeorder.PlaceOrderDto;
import com.example.eshop.cart.domain.checkout.delivery.DeliveryService;
import com.example.eshop.cart.domain.checkout.delivery.DeliveryServiceNotFoundException;
import com.example.eshop.cart.domain.checkout.payment.PaymentServiceNotAvailableException;
import com.example.eshop.cart.domain.checkout.payment.PaymentServiceNotFoundException;
import com.example.eshop.cart.domain.checkout.delivery.ShipmentNotAvailableException;
import com.example.eshop.cart.domain.checkout.payment.PaymentService;

public interface OrderFactory {
    /**
     * Factory for creating {@link Order} from {@link Cart}. It ensures delivery and
     * payment availability, but can leave {@link Order} in inconsistent (incomplete)
     * state for intermediate Checkout Process.
     *
     * @throws DeliveryServiceNotFoundException if {@code deliveryServiceId} is not null and {@link DeliveryService}
     *         with {@code deliveryServiceId} does not exist
     * @throws ShipmentNotAvailableException if {@code deliveryServiceId} is not null and {@link DeliveryService}
     *         with {@code deliveryServiceId} can't be applied to this order
     * @throws PaymentServiceNotFoundException if {@code paymentServiceId} is not null and {@link PaymentService}
     *          with {@code paymentServiceId} does not exist
     * @throws PaymentServiceNotAvailableException if {@code paymentServiceId} is not null and {@link PaymentService}
     *         with {@code paymentServiceId} can't be applied to this order
     */
    Order create(PlaceOrderDto placeOrderDto);
}
