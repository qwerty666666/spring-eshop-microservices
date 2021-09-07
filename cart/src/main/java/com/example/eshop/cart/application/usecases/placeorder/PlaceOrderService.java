package com.example.eshop.cart.application.usecases.placeorder;

import com.example.eshop.cart.application.usecases.checkout.OrderDto;
import com.example.eshop.cart.domain.checkout.order.Order;
import com.example.eshop.cart.domain.checkout.delivery.DeliveryService;
import com.example.eshop.cart.domain.checkout.delivery.DeliveryServiceNotFoundException;
import com.example.eshop.cart.domain.checkout.payment.PaymentServiceNotAvailableException;
import com.example.eshop.cart.domain.checkout.payment.PaymentServiceNotFoundException;
import com.example.eshop.cart.domain.checkout.delivery.ShipmentNotAvailableException;
import com.example.eshop.cart.domain.checkout.payment.PaymentService;
import com.example.eshop.sharedkernel.domain.validation.ValidationException;

public interface PlaceOrderService {
    /**
     * Creates new order from customer's Cart.
     *
     * Customer's Cart will be cleared if order will be placed successfully.
     *
     * @throws DeliveryServiceNotFoundException if {@code deliveryServiceId} is not null and {@link DeliveryService}
     *         with {@code deliveryServiceId} does not exist
     * @throws ShipmentNotAvailableException if {@code deliveryServiceId} is not null and {@link DeliveryService}
     *         with {@code deliveryServiceId} can't be applied to this order
     * @throws PaymentServiceNotFoundException if {@code paymentServiceId} is not null and {@link PaymentService}
     *          with {@code paymentServiceId} does not exist
     * @throws PaymentServiceNotAvailableException if {@code paymentServiceId} is not null and {@link PaymentService}
     *         with {@code paymentServiceId} can't be applied to this order
     * @throws ValidationException if some fields are invalid
     */
    Order place(OrderDto orderDto);
}
