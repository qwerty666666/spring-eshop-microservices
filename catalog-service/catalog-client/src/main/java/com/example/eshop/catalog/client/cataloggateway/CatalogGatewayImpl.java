package com.example.eshop.catalog.client.cataloggateway;

import com.example.eshop.catalog.client.api.ProductsApi;
import com.example.eshop.catalog.client.api.model.PagedProductList;
import com.example.eshop.catalog.client.api.model.Product;
import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class CatalogGatewayImpl implements CatalogGateway {
    private final ProductsApi productsApi;

    @Override
    public Mono<Product> getProductByEan(Ean ean) {
        return getProductsByEan(List.of(ean))
                .flatMap(products -> Mono.justOrEmpty(products.get(ean)));
    }

    @Override
    public Mono<Map<Ean, Product>> getProductsByEan(List<Ean> eanList) {
        return requestProducts(eanList).map(products -> {
                // collect to map: EAN -> Product
                var eanProductMap = new HashMap<Ean, Product>();
                var existedEan = new HashSet<>(eanList);
                for (var product: products) {
                    for (var sku: product.getSku()) {
                        var ean = Ean.fromString(sku.getEan());
                        if (existedEan.contains(ean)) {
                            eanProductMap.put(ean, product);
                        }
                    }
                }

                // if there are EANs for which product is not found, map them to null
                if (eanProductMap.size() < eanList.size()) {
                    for (var ean: eanList) {
                        eanProductMap.computeIfAbsent(ean, e -> null);
                    }
                }

                return eanProductMap;
        });
    }

    private Mono<List<Product>> requestProducts(List<Ean> ean) {
        var eanStrings = ean.stream().map(Ean::toString).toList();

        return productsApi.getProductList(eanStrings.size(), 1, eanStrings)
                .retry(3)    // just to try other instances from load balancer
                .map(PagedProductList::getItems);
    }
}
