package com.example.eshop.checkout.client.events.orderplacedevent;

import java.util.Objects;

public record PaymentDto(
        PaymentServiceDto paymentService
) {
    public PaymentDto {
        Objects.requireNonNull(paymentService, "paymentService is required");
    }
}
