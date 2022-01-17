package com.example.eshop.catalog.application.product;

import com.example.eshop.catalog.domain.product.Product.ProductId;
import org.springframework.lang.Nullable;

public class ProductNotFoundException extends RuntimeException {
    private final ProductId productId;

    // TODO remove this constructor
    public ProductNotFoundException(String message) {
        this(null, message);
    }

    public ProductNotFoundException(@Nullable ProductId productId, String message) {
        super(message);
        this.productId = productId;
    }

    @Nullable
    public ProductId getProductId() {
        return productId;
    }
}
