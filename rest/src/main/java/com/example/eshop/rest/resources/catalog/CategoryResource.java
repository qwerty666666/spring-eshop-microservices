package com.example.eshop.rest.resources.catalog;

import com.example.eshop.catalog.domain.category.Category;
import org.springframework.lang.Nullable;

public class CategoryResource {
    public String id;
    public String name;
    @Nullable
    public String parentId;

    public CategoryResource(Category category) {
        this.id = category.getId().toString();
        this.name = category.getName();
        this.parentId = category.getParent() != null ? category.getParent().getId().toString() : null;
    }
}