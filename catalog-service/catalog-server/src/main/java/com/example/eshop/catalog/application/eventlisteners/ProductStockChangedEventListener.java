package com.example.eshop.catalog.application.eventlisteners;

import com.example.eshop.catalog.domain.product.ProductRepository;
import com.example.eshop.warehouse.client.WarehouseApi;
import com.example.eshop.warehouse.client.events.ProductStockChangedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Lazy(false) // disable for devtools lazy-loading, as we want start consuming on startup
@Slf4j
public class ProductStockChangedEventListener {
    private final ProductRepository productRepository;

    public ProductStockChangedEventListener(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @KafkaListener(
            topics = WarehouseApi.STOCK_CHANGED_TOPIC,
            containerFactory = "productStockChangedKafkaListenerContainerFactory"
    )
    @Transactional
    public void changeSkuQuantity(ProductStockChangedEvent event) {
        productRepository.findByEan(event.ean())
                .ifPresent(product -> {
                    log.trace("Handle ProductStockChangedEvent " + event);

                    product.setSkuAvailableQuantity(event.ean(), event.newQuantity());
                });
    }
}
