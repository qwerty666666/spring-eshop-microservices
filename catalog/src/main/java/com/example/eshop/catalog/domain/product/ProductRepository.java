package com.example.eshop.catalog.domain.product;

import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaRepository;
import com.example.eshop.catalog.domain.category.Category;
import com.example.eshop.catalog.domain.product.Product.ProductId;
import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import javax.persistence.QueryHint;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends EntityGraphJpaRepository<Product, ProductId>,
        PagingAndSortingRepository<Product, ProductId> {
    /**
     * Finds {@link Product} by given {@link Category}
     */
    @Query("select distinct p from Product p join p.categories pc where pc.category = :category")
    @QueryHints(@QueryHint(name = org.hibernate.jpa.QueryHints.HINT_PASS_DISTINCT_THROUGH, value = "false"))
    Page<Product> findByCategory(@Param("category") Category category, Pageable pageable);

    /**
     * Finds {@link Product}s which have {@link Sku} with given {@code ean}
     */
    @Query("select distinct p from Product p join p.sku s where s.ean in :ean")
    @QueryHints(@QueryHint(name = org.hibernate.jpa.QueryHints.HINT_PASS_DISTINCT_THROUGH, value = "false"))
    List<Product> findByEan(@Param("ean") List<Ean> ean);

    /**
     * Finds {@link Product} which has {@link Sku} with given {@code ean}
     */
    default Optional<Product> findByEan(@Param("ean") Ean ean) {
        var products = findByEan(List.of(ean));

        return products.isEmpty() ? Optional.empty() : Optional.of(products.get(0));
    }
}
