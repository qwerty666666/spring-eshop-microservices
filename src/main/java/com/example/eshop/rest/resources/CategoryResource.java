package com.example.eshop.rest.resources;

import com.example.eshop.core.catalog.domain.Category;

public class CategoryResource {
    public String id;
    public String name;
    public String parentId;

    public CategoryResource(Category category) {
        this.id = category.id().toString();
        this.name = category.getName();
        this.parentId = category.getParent() == null ? null : category.getParent().id().toString();
    }
}
