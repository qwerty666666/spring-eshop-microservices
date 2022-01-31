package com.example.eshop.catalog.client.cataloggateway;

import com.example.eshop.catalog.client.api.model.Product;
import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import reactor.core.publisher.Mono;
import java.util.List;
import java.util.Map;

/**
 * Gateway to Catalog microservice
 */
public interface CatalogGateway {
    /**
     * Returns product by given EAN and {@code Mono.empty} if product with
     * given EAN does not found.
     */
    Mono<Product> getProductByEan(Ean ean);

    /**
     * Returns products by given EANs. If for any EAN there is no product
     * then this EAN will be mapped to {@code null}.
     */
    Mono<Map<Ean, Product>> getProductsByEan(List<Ean> ean);
}
