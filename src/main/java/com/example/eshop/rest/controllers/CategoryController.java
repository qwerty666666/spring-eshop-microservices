package com.example.eshop.rest.controllers;

import com.example.eshop.core.catalog.application.CategoryCrudService;
import com.example.eshop.core.catalog.application.CategoryNotFoundException;
import com.example.eshop.rest.resources.CategoryResource;
import com.example.eshop.rest.resources.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    private final CategoryCrudService categoryCrudService;

    public CategoryController(CategoryCrudService categoryCrudService) {
        this.categoryCrudService = categoryCrudService;
    }

    @GetMapping
    public List<CategoryResource> getList() {
        var categories = categoryCrudService.getAll();

        return categories.stream().map(CategoryResource::new).toList();
    }

    @GetMapping("{id}")
    public Object getById(@PathVariable UUID id) {
        try {
            var category = categoryCrudService.getCategory(id);

            return new CategoryResource(category);
        } catch (CategoryNotFoundException e) {
            return new ResponseEntity<>(
                    new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Category " + id + " not found"),
                    HttpStatus.NOT_FOUND
            );
        }
    }
}
