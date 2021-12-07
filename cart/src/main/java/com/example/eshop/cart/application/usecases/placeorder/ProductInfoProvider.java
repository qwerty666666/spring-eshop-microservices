package com.example.eshop.cart.application.usecases.placeorder;

import com.example.eshop.cart.domain.cart.Cart;
import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import java.util.Map;

/**
 * Provide catalog information about products in Cart.
 */
public interface ProductInfoProvider {
    /**
     * @return information about all products in Cart.
     *
     * @throws NotExistedProductException if some product in the Cart does not exist
     *      in catalog
     */
    Map<Ean, ProductInfo> getProductsInfo(Cart cart);
}
