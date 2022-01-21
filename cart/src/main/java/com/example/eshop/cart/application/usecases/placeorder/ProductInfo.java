package com.example.eshop.cart.application.usecases.placeorder;

import java.util.List;

public record ProductInfo(
        String productName,
        List<String> images,
        List<ProductAttribute> attributes
) {
}
