package com.example.eshop.catalog.domain.product;

import com.cosium.spring.data.jpa.entity.graph.domain.EntityGraph;
import com.cosium.spring.data.jpa.entity.graph.domain.EntityGraphs;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaRepository;
import com.example.eshop.catalog.domain.category.Category;
import com.example.eshop.catalog.domain.product.Product.ProductId;
import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends EntityGraphJpaRepository<Product, ProductId>,
        PagingAndSortingRepository<Product, ProductId> {
    @Query("select distinct p from Product p join p.categories pc where pc.category = :category")
    Page<Product> findByCategory(@Param("category") Category category, Pageable pageable, EntityGraph entityGraph);

    @Query("select distinct p from Product p join p.sku s where s.ean in :ean")
    List<Product> findByEan(@Param("ean") List<Ean> ean, EntityGraph entityGraph);

    default Optional<Product> findByEan(@Param("ean") Ean ean) {
        var products = findByEan(List.of(ean), EntityGraphs.empty());

        return products.isEmpty() ? Optional.empty() : Optional.of(products.get(0));
    }
}
