package com.example.eshop.catalog.domain.category;

import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaRepository;
import com.example.eshop.catalog.domain.category.Category.CategoryId;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends EntityGraphJpaRepository<Category, CategoryId> {
}
