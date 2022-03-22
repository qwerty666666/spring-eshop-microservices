package com.example.eshop.checkout.utils;

import com.example.eshop.catalog.client.model.ProductDto;
import java.util.UUID;

public final class TestDataUtils {
    public static ProductDto fakeProduct() {
        return ProductDto.builder()
                .id(UUID.randomUUID().toString())
                .name("test")
                .build();
    }
}
