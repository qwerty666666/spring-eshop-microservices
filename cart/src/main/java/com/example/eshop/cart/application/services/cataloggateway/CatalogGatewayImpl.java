package com.example.eshop.cart.application.services.cataloggateway;

import com.example.eshop.catalog.client.api.ProductsApi;
import com.example.eshop.catalog.client.api.model.Product;
import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CatalogGatewayImpl implements CatalogGateway {
    private final ProductsApi productsApi;

    @Override
    public Product getProductByEan(Ean ean) {
        var products = getProductsByEan(List.of(ean));

        return Optional.ofNullable(products.get(ean))
                .orElseThrow(() -> new ProductNotFoundException("Product for EAN " + ean + " does not exist"));
    }

    @Override
    public Map<Ean, Product> getProductsByEan(List<Ean> eanList) {
        var products = requestProducts(eanList);

        var eanProductMap = new HashMap<Ean, Product>();
        for (var product: products) {
            for (var sku: product.getSku()) {
                eanProductMap.put(Ean.fromString(sku.getEan()), product);
            }
        }

        // if there are EANs for which product is not found, map
        // them to null
        if (eanProductMap.size() < eanList.size()) {
            for (var ean: eanList) {
                eanProductMap.computeIfAbsent(ean, e -> null);
            }
        }

        return eanProductMap;
    }

    private List<Product> requestProducts(List<Ean> ean) {
        // TODO what if service is unavailable ?? when it return null ??
        return productsApi.getProductList(ean.size(), 1, ean.stream().map(Ean::toString).toList())
                .block()
                .getItems();
    }
}
