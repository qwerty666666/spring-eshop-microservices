package com.example.eshop.cart.application.usecases.query.dto;

import com.example.eshop.cart.domain.Cart;
import java.util.List;

public record CartDto(String id, List<CartItemDto> items) {
    public CartDto(Cart cart) {
        this(cart.id().toString(), cart.getItems().stream().map(CartItemDto::new).toList());
    }
}
