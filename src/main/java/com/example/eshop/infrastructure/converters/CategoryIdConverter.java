package com.example.eshop.infrastructure.converters;

import com.example.eshop.core.catalog.domain.Category.CategoryId;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
public class CategoryIdConverter implements Converter<String, CategoryId> {
    @Override
    public CategoryId convert(String source) {
        return new CategoryId(UUID.fromString(source));
    }
}
