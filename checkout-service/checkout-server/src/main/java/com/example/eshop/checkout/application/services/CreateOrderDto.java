package com.example.eshop.checkout.application.services;

import com.example.eshop.cart.client.model.CartDto;
import com.example.eshop.checkout.domain.delivery.DeliveryService.DeliveryServiceId;
import com.example.eshop.checkout.domain.order.DeliveryAddress;
import com.example.eshop.checkout.domain.payment.PaymentService.PaymentServiceId;
import com.example.eshop.sharedkernel.domain.Assertions;
import lombok.Builder;
import org.springframework.lang.Nullable;

public record CreateOrderDto(
        String customerId,
        CartDto cart,
        DeliveryAddress address,
        @Nullable DeliveryServiceId deliveryServiceId,
        @Nullable PaymentServiceId paymentServiceId
) {
    public static final String CART_FIELD = "cart";
    public static final String DELIVERY_SERVICE_ID_FIELD = "deliveryServiceId";
    public static final String PAYMENT_SERVICE_ID_FIELD = "paymentServiceId";
    public static final String ADDRESS_FIELD = "address";
    public static final String CUSTOMER_ID_FIELD = "customerId";

    @Builder
    public CreateOrderDto {
        Assertions.notNull(customerId, "customerId must be not null");
        Assertions.notNull(cart, "cart must be not null");
        Assertions.notNull(address, "address must be not null");
    }
}
