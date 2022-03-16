package com.example.eshop.catalog.client;

import com.example.eshop.catalog.client.model.ProductDto;
import com.example.eshop.catalog.client.model.SkuDto;
import com.example.eshop.catalog.client.model.SkuInfoDto;
import com.example.eshop.catalog.client.model.SkuWithProductDto;
import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
public class CatalogServiceImpl implements CatalogService {
    private static final String API_PREFIX = "/api";
    private static final String SKU_URL = API_PREFIX + "/sku";

    private final WebClient webClient;

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
                                .orElseThrow(() -> new RuntimeException("CatalogService response error. Product " +
                                        sku.getProductId() + " not found in response."));

                        skuMap.put(sku.getEan(), createSkuWithProduct(sku, product));
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
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(SKU_URL)
                        .queryParam("ean", ean)
                        .build()
                )
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(SkuInfoDto.class);
    }
}
