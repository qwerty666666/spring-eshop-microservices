package com.example.eshop.rest.resources;

import com.example.eshop.catalog.domain.product.Sku;

public class SkuResource {
    public String ean;
    public MoneyResource price;

    public SkuResource(Sku sku) {
        this.ean = sku.getEan();
        this.price = new MoneyResource(sku.getPrice());
    }
}
