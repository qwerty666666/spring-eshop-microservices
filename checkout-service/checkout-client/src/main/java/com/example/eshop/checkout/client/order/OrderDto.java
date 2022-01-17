package com.example.eshop.checkout.client.order;

import com.example.eshop.sharedkernel.domain.valueobject.Money;
import java.util.UUID;

public record OrderDto(
        UUID id,
        String customerId,
        CartDto cart,
        Money totalPrice,
        String deliveryServiceId,
        String paymentServiceId,
        DeliveryAddressDto address
) {
}
