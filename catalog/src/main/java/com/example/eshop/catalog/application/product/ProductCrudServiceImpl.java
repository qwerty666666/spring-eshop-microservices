package com.example.eshop.catalog.application.product;

import com.example.eshop.catalog.application.category.CategoryCrudService;
import com.example.eshop.catalog.domain.category.Category.CategoryId;
import com.example.eshop.catalog.domain.product.Product;
import com.example.eshop.catalog.domain.product.Product.ProductId;
import com.example.eshop.catalog.domain.product.ProductRepository;
import com.example.eshop.catalog.domain.product.Sku;
import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
class ProductCrudServiceImpl implements ProductCrudService {

    // TODO return DTO from methods instead of call fetchAssociations() =D

    private final ProductRepository productRepository;
    private final CategoryCrudService categoryCrudService;
    private final EntityManager em;

    @Override
    @Transactional
    public Product getById(ProductId productId) {
        var product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId,
                        String.format("Product with ID %s not found", productId))
                );

        fetchAssociations(List.of(product));

        return product;
    }

    @Override
    @Transactional
    public Page<Product> getList(Pageable pageable) {
        var productPage = productRepository.findAll(pageable);

        fetchAssociations(productPage.getContent());

        return productPage;
    }

    @Override
    @Transactional
    public Page<Product> getByCategory(CategoryId categoryId, Pageable pageable) {
        var category = categoryCrudService.getCategory(categoryId);
        var productPage = productRepository.findByCategory(category, pageable);

        fetchAssociations(productPage.getContent());

        return productPage;
    }

    @Override
    @Transactional
    public Map<Ean, Product> getByEan(List<Ean> ean) {
        if (ean.isEmpty()) {
            return Collections.emptyMap();
        }

        var products = productRepository.findByEan(ean);

        fetchAssociations(products);

        var eanProductMap = products.stream()
                .flatMap(product -> product.getSku().stream())
                .collect(Collectors.toMap(Sku::getEan, Sku::getProduct));

        return ean.stream()
                .collect(HashMap::new, (map, e) -> map.put(e, eanProductMap.get(e)), HashMap::putAll);
    }

    private void fetchAssociations(List<Product> products) {
        fetchImages(products);
        fetchSku(products);
    }

    private void fetchSku(List<Product> products) {
        em.createQuery("""
                select product from Product product
                    join fetch product.sku sku
                    join fetch sku.attributes attr_value
                    join fetch attr_value.attribute attr
                    where product in :products"""
        )
                .setParameter("products", products)
                .getResultList();
    }

    private void fetchImages(List<Product> products) {
        em.createQuery("select p from Product p join fetch p.images where p in :products")
                .setParameter("products", products)
                .getResultList();
    }
}
