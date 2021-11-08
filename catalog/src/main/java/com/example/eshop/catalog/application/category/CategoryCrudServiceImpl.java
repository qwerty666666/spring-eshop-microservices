package com.example.eshop.catalog.application.category;

import com.cosium.spring.data.jpa.entity.graph.domain.EntityGraphs;
import com.example.eshop.catalog.domain.category.Category;
import com.example.eshop.catalog.domain.category.Category.CategoryId;
import com.example.eshop.catalog.domain.category.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
class CategoryCrudServiceImpl implements CategoryCrudService {
    private final CategoryRepository categoryRepository;

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

    @Override
    @Transactional
    public List<Category> getTree() {
        var categories = categoryRepository.findAll(EntityGraphs.named("Category.children"));

        return StreamSupport.stream(categories.spliterator(), false)
                .filter(category -> category.getParent() == null)
                .toList();
    }
}
