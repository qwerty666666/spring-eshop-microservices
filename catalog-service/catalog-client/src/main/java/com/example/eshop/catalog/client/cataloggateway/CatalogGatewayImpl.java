package com.example.eshop.catalog.client.cataloggateway;

import com.example.eshop.catalog.client.api.ProductsApi;
import com.example.eshop.catalog.client.api.model.ProductDto;
import com.example.eshop.catalog.client.api.model.SkuDto;
import com.example.eshop.catalog.client.api.model.SkuInfoDto;
import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
public class CatalogGatewayImpl implements CatalogGateway {
    private static final int RETRIES = 3;

    private final ProductsApi productsApi;

    @Override
    public Mono<SkuWithProductDto> getSku(Ean ean) {
        return getSku(List.of(ean))
                .flatMap(products -> Mono.justOrEmpty(products.get(ean)));
    }

    @Override
    public Mono<Map<Ean, SkuWithProductDto>> getSku(List<Ean> eanList) {
        return requestSku(eanList)
                .map(skuList -> {
                    var skuMap = new HashMap<Ean, SkuWithProductDto>();

                    for (var sku: skuList.getSku()) {
                        var product = Optional.ofNullable(skuList.getProducts().get(sku.getProductId()))
                                .orElseThrow(() -> new RuntimeException("CatalogGateway response error. Product " +
                                        sku.getProductId() + " not found in response."));

                        skuMap.put(Ean.fromString(sku.getEan()), createSkuWithProduct(sku, product));
                    }

                    // if there are EANs for which sku is not found, map them to null
                    if (skuMap.size() < eanList.size()) {
                        for (var ean: eanList) {
                            skuMap.computeIfAbsent(ean, e -> null);
                        }
                    }

                    return skuMap;
                });
    }

    private SkuWithProductDto createSkuWithProduct(SkuDto sku, ProductDto product) {
        return new SkuWithProductDto(sku.getPrice(), sku.getEan(), sku.getProductId(), sku.getQuantity(),
                sku.getAttributes(), product);
    }

    private Mono<SkuInfoDto> requestSku(List<Ean> ean) {
        var eanStrings = ean.stream().map(Ean::toString).toList();

        return productsApi.getSku(eanStrings)
                .retry(RETRIES);   // just to try other instances from load balancer
    }
}
