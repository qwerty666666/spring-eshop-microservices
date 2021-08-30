package com.example.eshop.cart.application.usecases.query.dto;

import com.example.eshop.cart.domain.CartItem;
import com.example.eshop.sharedkernel.domain.valueobject.Ean;

public record CartItemDto(Ean ean, int quantity) {
    public CartItemDto(CartItem cartItem) {
        this(cartItem.getEan(), cartItem.getQuantity());
    }
}
