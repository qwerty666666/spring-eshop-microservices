package com.example.eshop.checkout.client.events.orderplacedevent;

import com.example.eshop.catalog.client.SkuWithProductDto;
import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import com.example.eshop.sharedkernel.domain.valueobject.Money;
import java.util.Objects;

/**
 * @param ean
 * @param price total price
 * @param quantity
 * @param sku
 */
public record CartItemDto(
        Ean ean,
        Money price,
        int quantity,
        SkuWithProductDto sku
) {
    public CartItemDto {
        Objects.requireNonNull(ean, "ean is required");
        Objects.requireNonNull(price, "price is required");
        Objects.requireNonNull(sku, "sku is required");
    }
}
