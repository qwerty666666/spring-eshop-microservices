package com.example.eshop.catalog.rest.controllers;

import com.example.eshop.catalog.application.services.productcrudservice.ProductCrudService;
import com.example.eshop.catalog.application.services.productcrudservice.ProductNotFoundException;
import com.example.eshop.catalog.rest.api.ProductsApi;
import com.example.eshop.catalog.client.api.model.BasicError;
import com.example.eshop.catalog.client.api.model.PagedProductList;
import com.example.eshop.catalog.config.AppProperties;
import com.example.eshop.catalog.domain.product.Product;
import com.example.eshop.catalog.domain.product.Product.ProductId;
import com.example.eshop.catalog.rest.mappers.ProductMapper;
import com.example.eshop.catalog.rest.utils.BasicErrorBuilder;
import com.example.eshop.localizer.Localizer;
import com.example.eshop.sharedkernel.domain.validation.FieldError;
import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import com.example.eshop.sharedkernel.domain.valueobject.InvalidEanFormatException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping(AppProperties.REST_API_BASE_PATH_PROPERTY)
@RequiredArgsConstructor
@Getter(AccessLevel.PROTECTED)  // for access to autowired fields from @ExceptionHandler
public class ProductsController implements ProductsApi {
    private final ProductCrudService productCrudService;
    private final ProductMapper productMapper;
    private final Localizer localizer;

    /**
     * @return 404 response if Product doesn't exist
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    private BasicError handleProductNotFoundException(ProductNotFoundException e) {
        return BasicErrorBuilder.newInstance()
                .setStatus(HttpStatus.NOT_FOUND)
                .setDetail(getLocalizer().getMessage("productNotFound", e.getProductId()))
                .build();
    }

    @Override
    public ResponseEntity<com.example.eshop.catalog.client.api.model.Product> getProductById(String id) {
        var product = productCrudService.getById(new ProductId(id));

        return ResponseEntity.ok(productMapper.toProductDto(product));
    }

    @Override
    public ResponseEntity<PagedProductList> getProductList(Integer perPage, Integer page, List<String> ean) {
        var pageable = PageRequest.of(page - 1, perPage);

        Page<Product> products;

        if (ean != null && !ean.isEmpty()) {
            products = getProductListByEan(ean, pageable);
        } else {
            products = getProductList(pageable);
        }

        return ResponseEntity.ok(productMapper.toPagedProductListDto(products));
    }

    /**
     * Get products by given Ean list
     */
    private Page<Product> getProductListByEan(List<String> ean, Pageable pageable) {
        List<Ean> eanList;
        try {
            eanList = ean.stream().map(Ean::fromString).toList();
        } catch (InvalidEanFormatException e) {
            throw new InvalidMethodParameterException(new FieldError("ean", "invalidEanFormat", e.getEan()));
        }

        return productCrudService.getByEan(eanList, pageable);
    }

    /**
     * Get all products from catalog
     */
    private Page<Product> getProductList(Pageable pageable) {
        return productCrudService.getList(pageable);
    }
}
