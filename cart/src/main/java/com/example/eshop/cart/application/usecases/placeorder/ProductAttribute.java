package com.example.eshop.cart.application.usecases.placeorder;

import com.example.eshop.catalog.domain.product.AttributeValue;

public record ProductAttribute(
        long attributeId,
        String name,
        String value
) {
    public ProductAttribute(AttributeValue attributeValue) {
        this(
                attributeValue.getAttribute().getId(),
                attributeValue.getAttribute().getName(),
                attributeValue.getValue()
        );
    }
}
