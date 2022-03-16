package com.example.eshop.catalog.client;

import com.example.eshop.catalog.client.model.SkuWithProductDto;
import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import reactor.core.publisher.Mono;
import java.util.List;
import java.util.Map;

/**
 * Gateway to Catalog microservice
 */
public interface CatalogService {
    /**
     * Returns sku by given EAN and {@code Mono.empty} if sku with
     * given EAN does not found.
     */
    Mono<SkuWithProductDto> getSku(Ean ean);

    /**
     * Returns sku list by given EANs. If for any EAN there is no sku
     * then this EAN will be mapped to {@code null}.
     */
    Mono<Map<Ean, SkuWithProductDto>> getSku(List<Ean> ean);
}
