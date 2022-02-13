package com.example.eshop.cart.application.usecases.placeorder;

import com.example.eshop.cart.domain.cart.Cart;
import com.example.eshop.cart.domain.cart.CartItem;
import com.example.eshop.catalog.client.api.model.ImageDto;
import com.example.eshop.catalog.client.cataloggateway.SkuWithProductDto;
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

        var sku = catalogGateway.getSku(eanList)
                .blockOptional()
                .orElse(Collections.emptyMap());

        checkCartItemsExistence(eanList, sku);

        return sku.entrySet().stream()
                .collect(Collectors.toMap(Entry::getKey, e -> mapToProductInfo(e.getValue())));
    }

    /**
     * Checks that all items are found in catalog microservices
     */
    private void checkCartItemsExistence(List<Ean> requestedEanList, Map<Ean, SkuWithProductDto> foundSku) {
        var notExistedProducts = requestedEanList.stream()
                .filter(ean -> foundSku.get(ean) == null)
                .toList();

        if (!notExistedProducts.isEmpty()) {
            throw new NotExistedProductException(notExistedProducts, "Sku with EANs %s are not found in catalog"
                    .formatted(notExistedProducts.stream()
                            .map(Ean::toString)
                            .collect(Collectors.joining(",", "[ ", " ]"))
                    )
            );
        }
    }

    private ProductInfo mapToProductInfo(SkuWithProductDto sku) {
        var productName = sku.getProduct().getName();
        var images = sku.getProduct().getImages().stream()
                .map(ImageDto::getUrl)
                .toList();
        var attributes = sku.getAttributes().stream()
                .map(attr -> new ProductAttribute(Long.parseLong(attr.getId()), attr.getName(), attr.getValue()))
                .toList();

        return new ProductInfo(productName, images, attributes);
    }
}
