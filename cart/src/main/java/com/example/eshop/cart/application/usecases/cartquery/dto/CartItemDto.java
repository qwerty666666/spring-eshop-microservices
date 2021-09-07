package com.example.eshop.cart.application.usecases.cartquery.dto;

import com.example.eshop.cart.domain.cart.CartItem;
import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import com.example.eshop.sharedkernel.domain.valueobject.Money;

public record CartItemDto(Ean ean, Money price, int quantity, String productName) {
    public CartItemDto(CartItem cartItem) {
        this(cartItem.getEan(), cartItem.getPrice(), cartItem.getQuantity(), cartItem.getProductName());
    }
}
