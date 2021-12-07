package com.example.eshop.cart.application.usecases.placeorder;

import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import lombok.Getter;
import java.util.ArrayList;
import java.util.List;

/**
 * Product does not exist in Catalog
 */
@Getter
public class NotExistedProductException extends RuntimeException {
    private final List<Ean> unavailableProduct = new ArrayList<>();

    public NotExistedProductException(List<Ean> unavailableProduct, String message) {
        super(message);
        this.unavailableProduct.addAll(unavailableProduct);
    }
}
