package com.example.eshop.cart.application.usecases.placeorder;

import com.example.eshop.cart.domain.cart.Cart;
import com.example.eshop.cart.domain.cart.CartItem;
import com.example.eshop.catalog.application.product.ProductCrudService;
import com.example.eshop.catalog.domain.product.Product;
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
        var products = getProducts(cart);

        var productsInfo = new HashMap<Ean, ProductInfo>(products.size());
        for (var cartItem: cart.getItems()) {
            var ean = cartItem.getEan();
            var sku = products.get(ean).getSku(ean);

            productsInfo.put(ean, new ProductInfo(sku));
        }

        return productsInfo;
    }

    private Map<Ean, Product> getProducts(Cart cart) {
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

        return products;
    }
}
