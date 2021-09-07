package com.example.eshop.rest.resources.cart;

import com.example.eshop.cart.application.usecases.cartquery.dto.CartItemDto;
import com.example.eshop.rest.resources.shared.MoneyResource;

public record CartItemResource(
        String ean,
        MoneyResource price,
        int quantity,
        String productName) {
    public CartItemResource(CartItemDto item) {
        this(item.ean().toString(), new MoneyResource(item.price()), item.quantity(), item.productName());
    }
}
