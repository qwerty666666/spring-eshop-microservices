package com.example.eshop.checkout.client.order;

import com.example.eshop.catalog.client.api.model.Product;
import com.example.eshop.catalog.client.api.model.Sku;
import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import com.example.eshop.sharedkernel.domain.valueobject.Money;

public record CartItemDto(
        Ean ean,
        Money price,
        int quantity,
        Sku sku,
        Product product
) {
}
