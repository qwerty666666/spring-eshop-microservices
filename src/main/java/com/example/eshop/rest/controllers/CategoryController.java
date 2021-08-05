package com.example.eshop.rest.controllers;

import com.example.eshop.core.catalog.application.CategoryCrudService;
import com.example.eshop.core.catalog.application.CategoryNotFoundException;
import com.example.eshop.core.catalog.application.ProductCrudService;
import com.example.eshop.core.catalog.domain.category.Category.CategoryId;
import com.example.eshop.infrastructure.annotations.PageableSettings;
import com.example.eshop.rest.resources.CategoryResource;
import com.example.eshop.rest.resources.ErrorResponse;
import com.example.eshop.rest.resources.ProductListResource;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    public static final int PRODUCTS_DEFAULT_PAGE_SIZE = ProductController.DEFAULT_PAGE_SIZE;
    public static final int PRODUCTS_MAX_PAGE_SIZE = ProductController.MAX_PAGE_SIZE;

    private final CategoryCrudService categoryCrudService;
    private final ProductCrudService productCrudService;

    public CategoryController(CategoryCrudService categoryCrudService, ProductCrudService productCrudService) {
        this.categoryCrudService = categoryCrudService;
        this.productCrudService = productCrudService;
    }

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> handleCategoryNotFoundException(CategoryNotFoundException e) {
        return new ResponseEntity<>(
                new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Category " + e.getCategoryId() + " not found"),
                HttpStatus.NOT_FOUND
        );
    }

    @GetMapping
    public List<CategoryResource> getList() {
        var categories = categoryCrudService.getAll();

        return categories.stream().map(CategoryResource::new).toList();
    }

    @GetMapping("{id}")
    public Object getById(@PathVariable CategoryId id) {
        var category = categoryCrudService.getCategory(id);

        return new CategoryResource(category);
    }

    @GetMapping("{id}/products")
    public Object getProducts(
            @PathVariable CategoryId id,
            @PageableSettings(
                    maxPageSize = PRODUCTS_MAX_PAGE_SIZE,
                    defaultPageSize = PRODUCTS_DEFAULT_PAGE_SIZE
            ) Pageable pageable) {
        var products = productCrudService.getForCategory(id, pageable);

        return new ProductListResource(products);
    }
}
