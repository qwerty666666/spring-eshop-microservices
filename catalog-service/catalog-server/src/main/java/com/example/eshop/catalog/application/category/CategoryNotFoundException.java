package com.example.eshop.catalog.application.category;

import com.example.eshop.catalog.domain.category.Category.CategoryId;

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
