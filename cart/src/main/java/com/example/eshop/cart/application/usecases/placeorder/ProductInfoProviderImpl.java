package com.example.eshop.cart.application.usecases.placeorder;

import com.example.eshop.cart.domain.cart.Cart;
import com.example.eshop.cart.domain.cart.CartItem;
import com.example.eshop.catalog.application.product.ProductCrudService;
import com.example.eshop.catalog.domain.product.Sku;
import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductInfoProviderImpl implements ProductInfoProvider {
    private final ProductCrudService productCrudService;

    @Override
    public Map<Ean, ProductInfo> getProductsInfo(Cart cart) {
        var sku = getSkuForCart(cart);

        var productsInfo = new HashMap<Ean, ProductInfo>(sku.size());
        for (var cartItem: cart.getItems()) {
            var ean = cartItem.getEan();
            var productInfo = new ProductInfo(sku.get(ean));

            productsInfo.put(ean, productInfo);
        }

        return productsInfo;
    }

    private Map<Ean, Sku> getSkuForCart(Cart cart) {
        var eanList = cart.getItems().stream().map(CartItem::getEan).toList();
        var products = productCrudService.getByEan(eanList);

        // check that all products in Cart are existed in catalog
        var notExistedProducts = eanList.stream()
                .filter(ean -> !products.containsKey(ean))
                .toList();

        if (!notExistedProducts.isEmpty()) {
            throw new NotExistedProductException(notExistedProducts, "Product with EANs %s are not found in catalog"
                    .formatted(notExistedProducts.stream()
                            .map(Ean::toString)
                            .collect(Collectors.joining(",", "[ ", " ]"))
                    )
            );
        }

        return eanList.stream()
                .collect(Collectors.toMap(ean -> ean, ean -> products.get(ean).getSku(ean)));
    }
}
