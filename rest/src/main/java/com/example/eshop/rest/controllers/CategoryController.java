package com.example.eshop.rest.controllers;

import com.example.eshop.catalog.application.category.CategoryCrudService;
import com.example.eshop.catalog.application.category.CategoryNotFoundException;
import com.example.eshop.catalog.application.product.ProductCrudService;
import com.example.eshop.catalog.domain.category.Category.CategoryId;
import com.example.eshop.rest.api.CategoriesApi;
import com.example.eshop.rest.controllers.utils.BasicErrorBuilder;
import com.example.eshop.rest.dto.BasicErrorDto;
import com.example.eshop.rest.dto.CategoryDto;
import com.example.eshop.rest.dto.CategoryTreeItemDto;
import com.example.eshop.rest.dto.PagedProductListDto;
import com.example.eshop.rest.mappers.CategoryMapper;
import com.example.eshop.rest.mappers.ProductMapper;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Getter(AccessLevel.PROTECTED)  // for access to autowired fields from @ExceptionHandler
public class CategoryController implements CategoriesApi {
    private final CategoryCrudService categoryCrudService;
    private final ProductCrudService productCrudService;
    private final MessageSource messageSource;
    private final CategoryMapper categoryMapper;
    private final ProductMapper productMapper;

    /**
     * @return 404 response if Category doesn't exist
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    private BasicErrorDto handleCategoryNotFoundException(CategoryNotFoundException e, Locale locale) {
        return BasicErrorBuilder.newInstance()
                .setStatus(HttpStatus.NOT_FOUND)
                .setDetail(getMessageSource().getMessage("categoryNotFound", new Object[]{ e.getCategoryId() }, locale))
                .build();
    }

    @Override
    public ResponseEntity<CategoryDto> getCategoryById(String id) {
        var category = categoryCrudService.getCategory(new CategoryId(id));

        return ResponseEntity.ok(categoryMapper.toCategoryDto(category));
    }

    @Override
    public ResponseEntity<List<CategoryDto>> getCategoryList() {
        var categories = categoryCrudService.getAll();

        return ResponseEntity.ok(categoryMapper.toCategoryDtoList(categories));
    }

    @Override
    public ResponseEntity<List<CategoryTreeItemDto>> getCategoryTree() {
        var tree = categoryCrudService.getTree();

        return ResponseEntity.ok(categoryMapper.toTree(tree));
    }

    @Override
    public ResponseEntity<PagedProductListDto> getProductsByCategory(String id, Integer perPage, Integer page) {
        var pageable = PageRequest.of(page - 1, perPage);
        var products = productCrudService.getByCategory(new CategoryId(id), pageable);

        return ResponseEntity.ok(productMapper.toPagedProductListDto(products));
    }
}
