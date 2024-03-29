package com.example.eshop.catalog.rest.controllers;

import com.example.eshop.catalog.application.services.categorycrudservice.CategoryCrudService;
import com.example.eshop.catalog.application.services.categorycrudservice.CategoryNotFoundException;
import com.example.eshop.catalog.application.services.productcrudservice.ProductCrudService;
import com.example.eshop.catalog.rest.api.CategoriesApi;
import com.example.eshop.catalog.client.model.CategoryDto;
import com.example.eshop.catalog.client.model.CategoryTreeItemDto;
import com.example.eshop.catalog.client.model.PagedProductListDto;
import com.example.eshop.catalog.config.AppProperties;
import com.example.eshop.catalog.domain.category.Category.CategoryId;
import com.example.eshop.catalog.rest.mappers.CategoryMapper;
import com.example.eshop.catalog.rest.mappers.ProductMapper;
import com.example.eshop.localizer.Localizer;
import com.example.eshop.rest.models.BasicErrorDto;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
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

    private final Localizer localizer;

    /**
     * @return 404 response if Category doesn't exist
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    private BasicErrorDto handleCategoryNotFoundException(CategoryNotFoundException e, Locale locale) {
        return new BasicErrorDto(
                HttpStatus.NOT_FOUND.value(),
                getLocalizer().getMessage("categoryNotFound", e.getCategoryId())
        );
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
