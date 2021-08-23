package com.example.eshop.catalog.application.product;

import com.example.eshop.catalog.application.category.CategoryNotFoundException;
import com.example.eshop.catalog.domain.category.Category.CategoryId;
import com.example.eshop.catalog.domain.category.Category;
import com.example.eshop.catalog.domain.product.Product;
import com.example.eshop.catalog.domain.product.Product.ProductId;
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

    /**
     * Find Products for the given Category
     *
     * @throws CategoryNotFoundException if {@link Category} with given
     *                 {@code categoryId} doesn't exists
     */
    Page<Product> getForCategory(CategoryId categoryId, Pageable pageable);
}
