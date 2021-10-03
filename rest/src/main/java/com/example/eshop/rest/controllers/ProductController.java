package com.example.eshop.rest.controllers;

import com.example.eshop.catalog.application.product.ProductCrudService;
import com.example.eshop.catalog.application.product.ProductNotFoundException;
import com.example.eshop.catalog.domain.product.Product.ProductId;
import com.example.eshop.rest.api.ProductsApi;
import com.example.eshop.rest.controllers.utils.BasicErrorBuilder;
import com.example.eshop.rest.dto.BasicErrorDto;
import com.example.eshop.rest.dto.PagedProductListDto;
import com.example.eshop.rest.dto.ProductDto;
import com.example.eshop.rest.mappers.ProductMapper;
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
@RequestMapping("/api")
@RequiredArgsConstructor
@Getter(AccessLevel.PROTECTED)  // for access to autowired fields from @ExceptionHandler
public class ProductController implements ProductsApi {
    private final ProductCrudService productCrudService;
    private final MessageSource messageSource;
    private final ProductMapper productMapper;

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    private BasicErrorDto handleProductNotFoundException(ProductNotFoundException e, Locale locale) {
        return BasicErrorBuilder.newInstance()
                .setStatus(HttpStatus.NOT_FOUND)
                .setDetail(getMessageSource().getMessage("productNotFound", new Object[]{ e.getProductId() }, locale))
                .build();
    }

    @Override
    public ResponseEntity<ProductDto> getProductById(String id) {
        var product = productCrudService.getById(new ProductId(id));

        return ResponseEntity.ok(productMapper.toProductDto(product));
    }

    @Override
    public ResponseEntity<PagedProductListDto> getProductList(Integer perPage, Integer page) {
        var pageable = PageRequest.of(page - 1, perPage);
        var products = productCrudService.getList(pageable);

        return ResponseEntity.ok(productMapper.toPagedProductListDto(products));
    }
}
