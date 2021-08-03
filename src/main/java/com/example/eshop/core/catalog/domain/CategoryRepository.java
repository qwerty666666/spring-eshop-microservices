package com.example.eshop.core.catalog.domain;

import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface CategoryRepository extends EntityGraphJpaRepository<Category, UUID> {
}
