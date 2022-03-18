package com.example.eshop.cart.domain.cart;

import com.example.eshop.cart.client.model.CartDto;
import com.example.eshop.cart.client.model.CartItemDto;

public class CartMapper {
    private static CartMapper instance;

    public static CartMapper getInstance() {
        var localInstance = instance;

        if (localInstance == null) {
            synchronized (CartMapper.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new CartMapper();
                }
            }
        }

        return localInstance;
    }

    public CartDto toCartDto(Cart cart) {
        return new CartDto()
                .id(cart.getId() == null ? null : cart.getId().toString())
                .totalPrice(cart.getTotalPrice())
                .items(cart.getItems().stream()
                        .map(item -> new CartItemDto()
                                .ean(item.getEan())
                                .price(item.getItemPrice())
                                .productName("test")
                                .quantity(item.getQuantity())
                                .availableQuantity(10)
                        )
                        .toList()
                );
    }
}
