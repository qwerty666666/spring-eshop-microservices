package com.example.eshop.rest.controllers;

import com.example.eshop.catalog.application.product.ProductCrudService;
import com.example.eshop.catalog.application.product.ProductNotFoundException;
import com.example.eshop.catalog.domain.product.Product.ProductId;
import com.example.eshop.rest.infrastructure.web.PageableSettings;
import com.example.eshop.rest.resources.shared.ErrorResponse;
import com.example.eshop.rest.resources.catalog.ProductListResource;
import com.example.eshop.rest.resources.catalog.ProductResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Locale;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    public static final int DEFAULT_PAGE_SIZE = 30;
    public static final int MAX_PAGE_SIZE = 50;

    @Autowired
    private ProductCrudService productCrudService;

    @Autowired
    private MessageSource messageSource;

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> handleProductNotFoundException(ProductNotFoundException e, Locale locale) {
        var message = messageSource.getMessage("productNotFound", new Object[]{ e.getProductId() }, locale);

        return new ResponseEntity<>(
                new ErrorResponse(HttpStatus.NOT_FOUND.value(), message),
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
