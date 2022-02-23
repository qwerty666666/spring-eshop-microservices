package com.example.eshop.catalog.application.services.productcrudservice;

import com.example.eshop.catalog.application.services.categorycrudservice.CategoryCrudService;
import com.example.eshop.catalog.domain.category.Category.CategoryId;
import com.example.eshop.catalog.domain.product.Product;
import com.example.eshop.catalog.domain.product.Product.ProductId;
import com.example.eshop.catalog.domain.product.ProductRepository;
import com.example.eshop.catalog.domain.product.Sku;
import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import lombok.RequiredArgsConstructor;
import org.hibernate.jpa.QueryHints;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
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
        var products = getByEan(ean, Pageable.unpaged()).getContent();

        var eanProductMap = products.stream()
                .flatMap(product -> product.getSku().stream())
                .collect(Collectors.toMap(Sku::getEan, Sku::getProduct));

        return ean.stream()
                .collect(HashMap::new, (map, e) -> map.put(e, eanProductMap.get(e)), HashMap::putAll);
    }

    @Override
    @Transactional
    public Page<Product> getByEan(List<Ean> ean, Pageable pageable) {
        if (ean.isEmpty()) {
            return Page.empty();
        }

        var productPage = productRepository.findByEan(ean, pageable);

        fetchAssociations(productPage.getContent());

        return productPage;
    }

    /**
     * Load lazy-load associations for products
     * {@see https://stackoverflow.com/a/30093606}
     */
    private void fetchAssociations(List<Product> products) {
        fetchImages(products);
        fetchSku(products);
    }

    private void fetchSku(List<Product> products) {
        if (products.isEmpty()) {
            return;
        }

        em.createQuery("""
                select distinct product
                    from Product product
                    left join fetch product.sku sku
                    left join fetch sku.attributes attr_value
                    left join fetch attr_value.attribute attr
                    where product in :products""",
                Product.class
        )
                .setParameter("products", products)
                .setHint(QueryHints.HINT_PASS_DISTINCT_THROUGH, false)
                .getResultList();
    }

    private void fetchImages(List<Product> products) {
        if (products.isEmpty()) {
            return;
        }

        em.createQuery("""
                select distinct p
                    from Product p
                    left join fetch p.images
                    where p in :products""",
                Product.class
        )
                .setParameter("products", products)
                .setHint(QueryHints.HINT_PASS_DISTINCT_THROUGH, false)
                .getResultList();
    }
}
