package com.example.eshop.cart.application.usecases.cartquery.dto;

import com.example.eshop.cart.domain.cart.Cart;
import java.util.List;

public record CartDto(String id, List<CartItemDto> items) {
    public CartDto(Cart cart) {
        this(cart.getId().toString(), cart.getItems().stream().map(CartItemDto::new).toList());
    }
}
