package com.example.eshop.rest.controllers;

import com.example.eshop.core.catalog.application.ProductCrudService;
import com.example.eshop.core.catalog.application.ProductNotFoundException;
import com.example.eshop.core.catalog.domain.Product.ProductId;
import com.example.eshop.rest.resources.ErrorResponse;
import com.example.eshop.rest.resources.ProductListResource;
import com.example.eshop.rest.resources.ProductResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    public static final int DEFAULT_PAGE_SIZE = 30;

    private final ProductCrudService productCrudService;

    @Autowired
    public ProductController(ProductCrudService productCrudService) {
        this.productCrudService = productCrudService;
    }

    @GetMapping("{id}")
    public Object getById(@PathVariable ProductId id) {
        try {
            var product = productCrudService.getProduct(id);

            return new ProductResource(product);
        } catch (ProductNotFoundException e) {
            return new ResponseEntity<>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Product " + id + " not found"),
                    HttpStatus.NOT_FOUND
            );
        }
    }

    @GetMapping("/")
    public ProductListResource getList(@PageableDefault(size = DEFAULT_PAGE_SIZE) Pageable pageable) {
        return new ProductListResource(productCrudService.getList(pageable));
    }
}
