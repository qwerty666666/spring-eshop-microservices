package com.example.eshop.rest.controllers;

import com.example.eshop.catalog.application.ProductCrudService;
import com.example.eshop.catalog.application.exceptions.ProductNotFoundException;
import com.example.eshop.catalog.domain.product.Product.ProductId;
import com.example.eshop.rest.infrastructure.web.PageableSettings;
import com.example.eshop.rest.resources.ErrorResponse;
import com.example.eshop.rest.resources.ProductListResource;
import com.example.eshop.rest.resources.ProductResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    public static final int DEFAULT_PAGE_SIZE = 30;
    public static final int MAX_PAGE_SIZE = 50;

    private final ProductCrudService productCrudService;

    @Autowired
    public ProductController(ProductCrudService productCrudService) {
        this.productCrudService = productCrudService;
    }

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> handleProductNotFoundException(ProductNotFoundException e) {
        return new ResponseEntity<>(
                new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Product " + e.getProductId() + " not found"),
                HttpStatus.NOT_FOUND
        );
    }

    @GetMapping("{id}")
    public Object getById(@PathVariable ProductId id) {
        var product = productCrudService.getProduct(id);
        return new ProductResource(product);
    }

    @GetMapping
    public ProductListResource getList(@PageableSettings(maxPageSize = MAX_PAGE_SIZE,
            defaultPageSize = DEFAULT_PAGE_SIZE) Pageable pageable) {
        var products = productCrudService.getList(pageable);
        return new ProductListResource(products);
    }
}
