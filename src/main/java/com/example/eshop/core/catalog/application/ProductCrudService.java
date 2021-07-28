package com.example.eshop.core.catalog.application;

import com.example.eshop.core.catalog.domain.Product;
import com.example.eshop.core.catalog.domain.Product.ProductId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductCrudService {
    /**
     * @throws ProductNotFoundException if product with given {@code productId} doesn't exists
     */
    Product getProduct(ProductId productId);

    /**
     * Find Products for given page
     */
    Page<Product> getList(Pageable pageable);
}
