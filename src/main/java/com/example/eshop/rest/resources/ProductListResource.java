package com.example.eshop.rest.resources;

import com.example.eshop.core.catalog.domain.Product;
import org.springframework.data.domain.Page;

public class ProductListResource extends PagedResource<ProductResource> {
    public ProductListResource(Page<Product> page) {
        super(page, ProductResource::new);
    }
}
