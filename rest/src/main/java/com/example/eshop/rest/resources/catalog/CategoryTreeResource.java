package com.example.eshop.rest.resources.catalog;

import com.example.eshop.catalog.domain.category.Category;
import java.util.List;

public class CategoryTreeResource {
    public String id;
    public String name;
    public List<CategoryTreeResource> children;

    public CategoryTreeResource(Category category) {
        this.id = category.id().toString();
        this.name = category.getName();
        this.children = category.getChildren().stream().map(CategoryTreeResource::new).toList();
    }

    public static List<CategoryTreeResource> treeOf(List<Category> rootCategories) {
        return rootCategories.stream().map(CategoryTreeResource::new).toList();
    }
}
