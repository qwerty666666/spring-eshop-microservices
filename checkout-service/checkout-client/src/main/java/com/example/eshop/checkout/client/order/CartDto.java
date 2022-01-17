package com.example.eshop.checkout.client.order;

import java.util.List;

public record CartDto(
        List<CartItemDto> items
) {
}
