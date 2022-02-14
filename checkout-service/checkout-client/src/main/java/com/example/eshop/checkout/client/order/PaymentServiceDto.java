package com.example.eshop.checkout.client.order;

import java.util.Objects;

public record PaymentServiceDto(
        String id,
        String name
) {
    public PaymentServiceDto {
        Objects.requireNonNull(id, "id is required");
        Objects.requireNonNull(name, "name is required");
    }
}
