package com.example.eshop.catalog.application.eventlisteners;

import com.example.eshop.catalog.domain.product.ProductRepository;
import com.example.eshop.warehouse.client.events.ProductStockChangedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ProductStockChangedEventListener {
    private final ProductRepository productRepository;

    public ProductStockChangedEventListener(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @EventListener
    @Transactional
    public void changeSkuQuantity(ProductStockChangedEvent event) {
        productRepository.findByEan(event.ean())
                .ifPresent(product -> product.setSkuAvailableQuantity(event.ean(), event.newQuantity()));
    }
}
