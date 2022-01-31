package com.example.eshop.cart.application.usecases.placeorder;

import com.example.eshop.cart.domain.cart.Cart;
import com.example.eshop.cart.domain.cart.CartItem;
import com.example.eshop.catalog.client.api.model.Image;
import com.example.eshop.catalog.client.api.model.Product;
import com.example.eshop.catalog.client.cataloggateway.CatalogGateway;
import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductInfoProviderImpl implements ProductInfoProvider {
    private final CatalogGateway catalogGateway;

    // TODO replace with Product DTO
    @Override
    public Map<Ean, ProductInfo> getProductsInfo(Cart cart) {
        var eanList = cart.getItems().stream().map(CartItem::getEan).toList();

        var products = catalogGateway.getProductsByEan(eanList)
                .blockOptional()
                .orElse(Collections.emptyMap());

        checkCartItemsExistence(eanList, products);

        return products.entrySet().stream()
                .collect(Collectors.toMap(Entry::getKey, e -> mapToProductInfo(e.getValue(), e.getKey())));
    }

    /**
     * Checks that all items are found in catalog microservices
     */
    private void checkCartItemsExistence(List<Ean> requestedEanList, Map<Ean, Product> foundProducts) {
        var notExistedProducts = requestedEanList.stream()
                .filter(ean -> foundProducts.get(ean) == null)
                .toList();

        if (!notExistedProducts.isEmpty()) {
            throw new NotExistedProductException(notExistedProducts, "Product with EANs %s are not found in catalog"
                    .formatted(notExistedProducts.stream()
                            .map(Ean::toString)
                            .collect(Collectors.joining(",", "[ ", " ]"))
                    )
            );
        }
    }

    private ProductInfo mapToProductInfo(Product product, Ean ean) {
        var sku = product.getSku().stream()
                .filter(s -> ean.toString().equals(s.getEan()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Sku with EAN " + ean + " does not exist in Product"));

        var attributes = sku.getAttributes().stream()
                .map(attr -> new ProductAttribute(Long.parseLong(attr.getId()), attr.getName(), attr.getValue()))
                .toList();

        var images = product.getImages().stream().map(Image::getUrl).toList();

        return new ProductInfo(product.getName(), images, attributes);
    }
}
