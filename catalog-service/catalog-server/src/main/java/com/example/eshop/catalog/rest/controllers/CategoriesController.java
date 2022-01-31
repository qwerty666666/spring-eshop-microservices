package com.example.eshop.catalog.rest.controllers;

import com.example.eshop.catalog.application.services.categorycrudservice.CategoryCrudService;
import com.example.eshop.catalog.application.services.categorycrudservice.CategoryNotFoundException;
import com.example.eshop.catalog.application.services.productcrudservice.ProductCrudService;
import com.example.eshop.catalog.rest.api.CategoriesApi;
import com.example.eshop.catalog.client.api.model.BasicError;
import com.example.eshop.catalog.client.api.model.Category;
import com.example.eshop.catalog.client.api.model.CategoryTreeItem;
import com.example.eshop.catalog.client.api.model.PagedProductList;
import com.example.eshop.catalog.config.AppProperties;
import com.example.eshop.catalog.domain.category.Category.CategoryId;
import com.example.eshop.catalog.rest.mappers.CategoryMapper;
import com.example.eshop.catalog.rest.mappers.ProductMapper;
import com.example.eshop.catalog.rest.utils.BasicErrorBuilder;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping(AppProperties.REST_API_BASE_PATH_PROPERTY)
@RequiredArgsConstructor
@Getter(AccessLevel.PROTECTED)  // for access to autowired fields from @ExceptionHandler
public class CategoriesController implements CategoriesApi {
    private final ProductCrudService productCrudService;
    private final CategoryCrudService categoryCrudService;

    private final ProductMapper productMapper;
    private final CategoryMapper categoryMapper;

    private final MessageSource messageSource;

    /**
     * @return 404 response if Category doesn't exist
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    private BasicError handleCategoryNotFoundException(CategoryNotFoundException e, Locale locale) {
        return BasicErrorBuilder.newInstance()
                .setStatus(HttpStatus.NOT_FOUND)
                .setDetail(getMessageSource().getMessage("categoryNotFound", new Object[]{ e.getCategoryId() }, locale))
                .build();
    }

    @Override
    public ResponseEntity<Category> getCategoryById(String id) {
        var category = categoryCrudService.getCategory(new CategoryId(id));

        return ResponseEntity.ok(categoryMapper.toCategoryDto(category));
    }

    @Override
    public ResponseEntity<List<Category>> getCategoryList() {
        var categories = categoryCrudService.getAll();

        return ResponseEntity.ok(categoryMapper.toCategoryDtoList(categories));
    }

    @Override
    public ResponseEntity<List<CategoryTreeItem>> getCategoryTree() {
        var tree = categoryCrudService.getTree();

        return ResponseEntity.ok(categoryMapper.toTree(tree));
    }

    @Override
    public ResponseEntity<PagedProductList> getProductsByCategory(String id, Integer perPage, Integer page) {
        var pageable = PageRequest.of(page - 1, perPage);
        var products = productCrudService.getByCategory(new CategoryId(id), pageable);

        return ResponseEntity.ok(productMapper.toPagedProductListDto(products));
    }
}
