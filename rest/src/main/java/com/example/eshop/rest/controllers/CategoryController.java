package com.example.eshop.rest.controllers;

import com.example.eshop.catalog.application.category.CategoryCrudService;
import com.example.eshop.catalog.application.category.CategoryNotFoundException;
import com.example.eshop.catalog.application.product.ProductCrudService;
import com.example.eshop.catalog.domain.category.Category.CategoryId;
import com.example.eshop.rest.infrastructure.web.PageableSettings;
import com.example.eshop.rest.resources.catalog.CategoryResource;
import com.example.eshop.rest.resources.catalog.CategoryTreeResource;
import com.example.eshop.rest.resources.shared.ErrorResponse;
import com.example.eshop.rest.resources.catalog.ProductListResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    public static final int PRODUCTS_DEFAULT_PAGE_SIZE = ProductController.DEFAULT_PAGE_SIZE;
    public static final int PRODUCTS_MAX_PAGE_SIZE = ProductController.MAX_PAGE_SIZE;

    @Autowired
    private CategoryCrudService categoryCrudService;

    @Autowired
    private ProductCrudService productCrudService;

    @Autowired
    private MessageSource messageSource;

    /**
     * @return 404 response if Category doesn't exist
     */
    @ExceptionHandler
    private ResponseEntity<ErrorResponse> handleCategoryNotFoundException(CategoryNotFoundException e, Locale locale) {
        var message = messageSource.getMessage("categoryNotFound", new Object[]{ e.getCategoryId() }, locale);

        return new ResponseEntity<>(
                new ErrorResponse(HttpStatus.NOT_FOUND.value(), message),
                HttpStatus.NOT_FOUND
        );
    }

    /**
     * @return all categories
     */
    @GetMapping
    public List<CategoryResource> getList() {
        var categories = categoryCrudService.getAll();

        return categories.stream().map(CategoryResource::new).toList();
    }

    /**
     * @return category by given {@code id}
     */
    @GetMapping("/{id}")
    public CategoryResource getById(@PathVariable CategoryId id) {
        var category = categoryCrudService.getCategory(id);

        return new CategoryResource(category);
    }

    /**
     * @return product for the given {@code category}
     */
    @GetMapping("/{id}/products")
    public ProductListResource getProducts(
            @PathVariable CategoryId id,
            @PageableSettings(
                    maxPageSize = PRODUCTS_MAX_PAGE_SIZE,
                    defaultPageSize = PRODUCTS_DEFAULT_PAGE_SIZE
            ) Pageable pageable) {
        var products = productCrudService.getForCategory(id, pageable);

        return new ProductListResource(products);
    }

    /**
     * @return category hierarchy
     */
    @GetMapping("/tree")
    public List<CategoryTreeResource> getTree() {
        var tree = categoryCrudService.getTree();
        return CategoryTreeResource.treeOf(tree);
    }
}
