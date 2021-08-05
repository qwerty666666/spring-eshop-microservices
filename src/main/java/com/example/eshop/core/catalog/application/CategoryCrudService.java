package com.example.eshop.core.catalog.application;

import com.example.eshop.core.catalog.domain.Category;
import com.example.eshop.core.catalog.domain.Category.CategoryId;
import java.util.List;

public interface CategoryCrudService {
    /**
     * @throws CategoryNotFoundException if category {@code categoryId} not found
     */
    Category getCategory(CategoryId categoryId);

    /**
     * @return all {@link Category}
     */
    List<Category> getAll();
}
