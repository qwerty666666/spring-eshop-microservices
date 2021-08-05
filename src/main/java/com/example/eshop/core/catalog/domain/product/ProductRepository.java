package com.example.eshop.core.catalog.domain.product;

import com.cosium.spring.data.jpa.entity.graph.domain.EntityGraph;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaRepository;
import com.example.eshop.core.catalog.domain.category.Category;
import com.example.eshop.core.catalog.domain.product.Product.ProductId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends EntityGraphJpaRepository<Product, ProductId>,
        PagingAndSortingRepository<Product, ProductId> {
    @Query("select distinct p from Product p join p.categories pc where pc.category = :category")
    Page<Product> findByCategory(@Param("category") Category category, Pageable pageable, EntityGraph entityGraph);
}
