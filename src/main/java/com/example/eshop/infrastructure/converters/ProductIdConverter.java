package com.example.eshop.infrastructure.converters;

import com.example.eshop.core.catalog.domain.Product;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
public class ProductIdConverter implements Converter<String, Product.ProductId> {
    @Override
    public Product.ProductId convert(String source) {
        return new Product.ProductId(UUID.fromString(source));
    }
}
