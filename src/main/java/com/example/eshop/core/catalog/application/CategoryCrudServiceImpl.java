package com.example.eshop.core.catalog.application;

import com.cosium.spring.data.jpa.entity.graph.domain.EntityGraphs;
import com.example.eshop.core.catalog.domain.Category;
import com.example.eshop.core.catalog.domain.Category.CategoryId;
import com.example.eshop.core.catalog.domain.CategoryRepository;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
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
    public Category getCategory(CategoryId categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(categoryId, "Category " + categoryId + " not found"));
    }

    @Override
    @Transactional
    public List<Category> getAll() {
        var categories = categoryRepository.findAll(EntityGraphs.named("Category.parent"));
        return IterableUtils.toList(categories);
    }
}
