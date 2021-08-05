package com.example.eshop.core.catalog.domain;

import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaRepository;
import com.example.eshop.core.catalog.domain.Category.CategoryId;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends EntityGraphJpaRepository<Category, CategoryId> {
}
