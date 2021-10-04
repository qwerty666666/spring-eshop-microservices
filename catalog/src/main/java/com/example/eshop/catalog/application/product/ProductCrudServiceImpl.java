package com.example.eshop.catalog.application.product;

import com.cosium.spring.data.jpa.entity.graph.domain.EntityGraphs;
import com.example.eshop.catalog.application.category.CategoryCrudService;
import com.example.eshop.catalog.domain.category.Category;
import com.example.eshop.catalog.domain.category.Category.CategoryId;
import com.example.eshop.catalog.domain.product.Product;
import com.example.eshop.catalog.domain.product.Product.ProductId;
import com.example.eshop.catalog.domain.product.ProductRepository;
import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class ProductCrudServiceImpl implements ProductCrudService {
    private final ProductRepository productRepository;
    private final CategoryCrudService categoryCrudService;

    @Override
    @Transactional
    public Product getById(ProductId productId) {
        return productRepository.findById(productId, EntityGraphs.named("Product.skuAndImages"))
                .orElseThrow(() -> new ProductNotFoundException(productId,
                        String.format("Product with ID %s not found", productId))
                );
    }

    @Override
    @Transactional
    public Page<Product> getList(Pageable pageable) {
        return productRepository.findAll(pageable, EntityGraphs.named("Product.skuAndImages"));
    }

    @Override
    @Transactional
    public Page<Product> getByCategory(CategoryId categoryId, Pageable pageable) {
        Category category = categoryCrudService.getCategory(categoryId);

        return productRepository.findByCategory(category, pageable, EntityGraphs.named("Product.skuAndImages"));
    }

    @Override
    @Transactional
    public Product getByEan(Ean ean) {
        return productRepository.findByEan(ean, EntityGraphs.named("Product.skuAndImages"))
                .orElseThrow(() -> new ProductNotFoundException("Product with SKU " + ean + " not found"));
    }
}
