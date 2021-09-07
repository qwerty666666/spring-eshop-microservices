package com.example.eshop.cart.domain.checkout.placeorder;

import com.example.eshop.cart.domain.cart.Cart;
import com.example.eshop.cart.domain.checkout.delivery.DeliveryService.DeliveryServiceId;
import com.example.eshop.cart.domain.checkout.order.DeliveryAddress;
import com.example.eshop.cart.domain.checkout.payment.PaymentService.PaymentServiceId;
import lombok.Builder;
import org.springframework.lang.Nullable;

public record PlaceOrderDto(
        String customerId,
        Cart cart,
        DeliveryAddress address,
        @Nullable DeliveryServiceId deliveryServiceId,
        @Nullable PaymentServiceId paymentServiceId
) {
    @Builder
    public PlaceOrderDto {
    }
}
