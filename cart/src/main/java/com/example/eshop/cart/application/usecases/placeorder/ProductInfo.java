package com.example.eshop.cart.application.usecases.placeorder;

import com.example.eshop.catalog.domain.file.File;
import com.example.eshop.catalog.domain.product.Sku;
import java.util.List;

public record ProductInfo(
        String productName,
        List<String> images,
        List<ProductAttribute> attributes
) {
    public ProductInfo(Sku sku) {
        this(
                sku.getProduct().getName(),
                sku.getProduct().getImages().stream().map(File::getLocation).toList(),
                sku.getAttributeValues().stream().map(ProductAttribute::new).toList()
        );
    }
}
