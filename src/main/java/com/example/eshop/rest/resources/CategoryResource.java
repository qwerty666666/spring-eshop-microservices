package com.example.eshop.rest.resources;

import com.example.eshop.core.catalog.domain.category.Category;
import org.springframework.lang.Nullable;

public class CategoryResource {
    public String id;
    public String name;
    @Nullable
    public String parentId;

    public CategoryResource(Category category) {
        this.id = category.id().toString();
        this.name = category.getName();
        this.parentId = category.getParent() == null ? null : category.getParent().id().toString();
    }
}
