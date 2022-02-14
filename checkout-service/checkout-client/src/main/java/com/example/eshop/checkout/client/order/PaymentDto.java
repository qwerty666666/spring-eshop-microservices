package com.example.eshop.checkout.client.order;

import java.util.Objects;

public record PaymentDto(
        PaymentServiceDto paymentService
) {
    public PaymentDto {
        Objects.requireNonNull(paymentService, "paymentService is required");
    }
}
