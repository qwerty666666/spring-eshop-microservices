package com.example.eshop.checkout.client.events.orderplacedevent;

import com.example.eshop.sharedkernel.domain.valueobject.Money;
import java.util.List;
import java.util.Objects;

public record CartDto(
        Money price,
        List<CartItemDto> items
) {
    public CartDto {
        Objects.requireNonNull(price, "price is required");
        Objects.requireNonNull(items, "items is required");
    }
}
