package com.example.eshop.catalog.infrastructure.converters;

import com.example.eshop.catalog.domain.product.Product;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
public class ProductIdConverter implements Converter<String, Product.ProductId> {
    @Override
    @Nullable
    public Product.ProductId convert(String source) {
        return new Product.ProductId(Long.valueOf(source));
    }
}
