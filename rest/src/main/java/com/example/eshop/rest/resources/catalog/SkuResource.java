package com.example.eshop.rest.resources.catalog;

import com.example.eshop.catalog.domain.product.Sku;
import com.example.eshop.rest.resources.shared.MoneyResource;

public class SkuResource {
    public String ean;
    public MoneyResource price;

    public SkuResource(Sku sku) {
        this.ean = sku.getEan().toString();
        this.price = new MoneyResource(sku.getPrice());
    }
}
