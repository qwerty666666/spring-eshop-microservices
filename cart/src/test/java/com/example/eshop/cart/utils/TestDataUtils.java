package com.example.eshop.cart.utils;

import com.example.eshop.cart.domain.cart.CartItem;
import com.example.eshop.catalog.client.SkuWithProductDto;
import com.example.eshop.catalog.client.api.model.MoneyDto;
import com.example.eshop.catalog.client.api.model.ProductDto;
import com.example.eshop.sharedkernel.domain.valueobject.Money;
import java.util.UUID;

public final class TestDataUtils {
    public static SkuWithProductDto toSkuWithProductDto(CartItem cartItem) {
        var product = fakeProduct();

        return SkuWithProductDto.builder()
                .ean(cartItem.getEan().toString())
                .price(toMoneyDto(cartItem.getTotalPrice()))
                .quantity(cartItem.getQuantity())
                .productId(product.getId())
                .product(product)
                .build();
    }

    public static MoneyDto toMoneyDto(Money money) {
        return new MoneyDto(money.getAmount(), money.getCurrency().toString());
    }

    public static ProductDto fakeProduct() {
        return ProductDto.builder()
                .id(UUID.randomUUID().toString())
                .name("test")
                .build();
    }
}
