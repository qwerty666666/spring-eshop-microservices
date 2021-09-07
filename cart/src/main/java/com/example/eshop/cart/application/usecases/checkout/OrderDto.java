package com.example.eshop.cart.application.usecases.checkout;

import com.example.eshop.cart.domain.checkout.order.DeliveryAddress;
import com.example.eshop.cart.domain.checkout.delivery.DeliveryService.DeliveryServiceId;
import com.example.eshop.cart.domain.checkout.payment.PaymentService.PaymentServiceId;
import org.springframework.lang.Nullable;

public record OrderDto(
        String customerId,
        DeliveryAddress address,
        @Nullable DeliveryServiceId deliveryServiceId,
        @Nullable PaymentServiceId paymentServiceId
) {

}
