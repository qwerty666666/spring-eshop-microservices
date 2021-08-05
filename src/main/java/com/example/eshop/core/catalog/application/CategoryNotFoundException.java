package com.example.eshop.core.catalog.application;

import com.example.eshop.core.catalog.domain.Category.CategoryId;

public class CategoryNotFoundException extends RuntimeException {
    private final CategoryId categoryId;

    public CategoryNotFoundException(CategoryId categoryId, String message) {
        super(message);
        this.categoryId = categoryId;
    }

    public CategoryId getCategoryId() {
        return categoryId;
    }
}
