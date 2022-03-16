package com.example.eshop.cart.utils;

import com.example.eshop.cart.domain.cart.CartItem;
import com.example.eshop.catalog.client.model.SkuWithProductDto;
import com.example.eshop.catalog.client.model.ProductDto;
import java.util.UUID;

public final class TestDataUtils {
    public static SkuWithProductDto toSkuWithProductDto(CartItem cartItem) {
        var product = fakeProduct();

        return SkuWithProductDto.builder()
                .ean(cartItem.getEan())
                .price(cartItem.getTotalPrice())
                .quantity(cartItem.getQuantity())
                .productId(product.getId())
                .product(product)
                .build();
    }

    public static ProductDto fakeProduct() {
        return ProductDto.builder()
                .id(UUID.randomUUID().toString())
                .name("test")
                .build();
    }
}
