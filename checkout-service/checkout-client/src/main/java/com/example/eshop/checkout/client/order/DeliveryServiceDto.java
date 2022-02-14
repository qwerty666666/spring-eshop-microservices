package com.example.eshop.checkout.client.order;

import java.util.Objects;

public record DeliveryServiceDto(
        String id,
        String name
) {
    public DeliveryServiceDto {
        Objects.requireNonNull(id, "id is required");
        Objects.requireNonNull(name, "name is required");
    }
}
