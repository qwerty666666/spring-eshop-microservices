package com.example.eshop.catalog.application.services.productcrudservice;

import com.example.eshop.catalog.application.services.categorycrudservice.CategoryNotFoundException;
import com.example.eshop.catalog.domain.category.Category.CategoryId;
import com.example.eshop.catalog.domain.category.Category;
import com.example.eshop.catalog.domain.product.Product;
import com.example.eshop.catalog.domain.product.Product.ProductId;
import com.example.eshop.catalog.domain.product.Sku;
import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ProductCrudService {
    /**
     * @throws ProductNotFoundException if product with given {@code productId} doesn't exists
     */
    Product getById(ProductId productId);

    /**
     * Find Products for given page
     */
    Page<Product> getList(Pageable pageable);

    /**
     * Find Products for the given Category
     *
     * @throws CategoryNotFoundException if {@link Category} with given
     *                 {@code categoryId} doesn't exists
     */
    Page<Product> getByCategory(CategoryId categoryId, Pageable pageable);

    /**
     * Find {@link Product} which has {@link Sku} with given {@code ean}
     *
     * @throws ProductNotFoundException if there is no such product
     */
    default Product getByEan(Ean ean) {
        return Optional.ofNullable(getByEan(List.of(ean)).get(ean))
                .orElseThrow(() -> new ProductNotFoundException("Product with SKU " + ean + " not found"));
    }

    /**
     * Find {@link Product} which has {@link Sku} with given {@code ean}.
     * If there is no Sku for some ean, then this ean will be mapped to null.
     */
    Map<Ean, Product> getByEan(List<Ean> ean);

    /**
     * Find {@link Product} which has {@link Sku} with given {@code ean}.
     */
    Page<Product> getByEan(List<Ean> ean, Pageable pageable);
}
