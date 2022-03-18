package com.example.eshop.checkout.client.events.orderplacedevent;

import com.example.eshop.cart.client.model.CartDto;
import com.example.eshop.sharedkernel.domain.valueobject.Money;
import lombok.Builder;
import java.util.Objects;
import java.util.UUID;

public record OrderDto(
        UUID id,
        String customerId,
        CartDto cart,
        Money totalPrice,
        DeliveryDto delivery,
        PaymentDto payment
) {
    @Builder
    public OrderDto {
        Objects.requireNonNull(id, "id is required");
        Objects.requireNonNull(customerId, "customerId is required");
        Objects.requireNonNull(cart, "cart is required");
        Objects.requireNonNull(totalPrice, "totalPrice is required");
        Objects.requireNonNull(delivery, "delivery is required");
        Objects.requireNonNull(payment, "payment is required");
    }
}
