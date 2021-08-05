package com.example.eshop.rest.resources;

import com.example.eshop.core.catalog.domain.product.Product;
import java.util.List;

public class ProductResource {
    public String id;

    public String name;

    public List<SkuResource> sku;

    public ProductResource(Product product) {
        this.id = product.id().toString();
        this.name = product.getName();
        this.sku = product.getSku().stream().map(SkuResource::new).toList();
    }
}
