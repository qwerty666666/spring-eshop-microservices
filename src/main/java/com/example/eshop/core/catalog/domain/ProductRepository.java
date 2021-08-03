package com.example.eshop.core.catalog.domain;

import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaRepository;
import com.example.eshop.core.catalog.domain.Product.ProductId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends EntityGraphJpaRepository<Product, ProductId>,
        PagingAndSortingRepository<Product, ProductId> {
}
