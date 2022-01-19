package com.example.eshop.catalog.rest.controllers;

import com.example.eshop.catalog.application.services.productcrudservice.ProductCrudService;
import com.example.eshop.catalog.application.services.productcrudservice.ProductNotFoundException;
import com.example.eshop.catalog.client.api.ProductsApi;
import com.example.eshop.catalog.client.api.model.BasicError;
import com.example.eshop.catalog.client.api.model.PagedProductList;
import com.example.eshop.catalog.client.api.model.Product;
import com.example.eshop.catalog.config.AppProperties;
import com.example.eshop.catalog.domain.product.Product.ProductId;
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
import java.util.Locale;

@RestController
@RequestMapping(AppProperties.REST_API_BASE_PATH_PROPERTY)
@RequiredArgsConstructor
@Getter(AccessLevel.PROTECTED)  // for access to autowired fields from @ExceptionHandler
public class ProductsController implements ProductsApi {
    private final ProductCrudService productCrudService;
    private final ProductMapper productMapper;
    private final MessageSource messageSource;

    /**
     * @return 404 response if Product doesn't exist
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    private BasicError handleProductNotFoundException(ProductNotFoundException e, Locale locale) {
        return BasicErrorBuilder.newInstance()
                .setStatus(HttpStatus.NOT_FOUND)
                .setDetail(getMessageSource().getMessage("productNotFound", new Object[]{ e.getProductId() }, locale))
                .build();
    }

    @Override
    public ResponseEntity<Product> getProductById(String id) {
        var product = productCrudService.getById(new ProductId(id));

        return ResponseEntity.ok(productMapper.toProductDto(product));
    }

    @Override
    public ResponseEntity<PagedProductList> getProductList(Integer perPage, Integer page) {
        var pageable = PageRequest.of(page - 1, perPage);
        var products = productCrudService.getList(pageable);

        return ResponseEntity.ok(productMapper.toPagedProductListDto(products));
    }
}
