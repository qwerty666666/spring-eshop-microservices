package com.example.eshop.catalog.application.category;

import com.example.eshop.catalog.domain.category.Category;
import com.example.eshop.catalog.domain.category.Category.CategoryId;
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

    /**
     * @return list of root nodes
     */
    List<Category> getTree();
}
