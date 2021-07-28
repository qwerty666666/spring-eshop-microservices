package com.example.eshop.core.catalog.domain;

import com.example.eshop.core.catalog.domain.Product.ProductId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, ProductId>,
        PagingAndSortingRepository<Product, ProductId> {
}
