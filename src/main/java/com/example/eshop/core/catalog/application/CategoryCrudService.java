package com.example.eshop.core.catalog.application;

import com.example.eshop.core.catalog.domain.Category;
import java.util.UUID;

public interface CategoryCrudService {
    /**
     * @throws CategoryNotFoundException if category {@code categoryId} not found
     */
    Category getCategory(UUID categoryId);
}
