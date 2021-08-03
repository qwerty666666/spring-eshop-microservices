package com.example.eshop.core.catalog.application;

import com.example.eshop.core.catalog.domain.Category;
import com.example.eshop.core.catalog.domain.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
class CategoryCrudServiceImpl implements CategoryCrudService {
    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryCrudServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional
    public Category getCategory(UUID categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException("Category " + categoryId + " not found"));
    }
}
