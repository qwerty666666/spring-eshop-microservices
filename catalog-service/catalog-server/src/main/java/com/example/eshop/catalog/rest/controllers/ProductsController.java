package com.example.eshop.catalog.rest.controllers;

import com.example.eshop.catalog.application.services.productcrudservice.ProductCrudService;
import com.example.eshop.catalog.application.services.productcrudservice.ProductNotFoundException;
import com.example.eshop.catalog.client.model.PagedProductListDto;
import com.example.eshop.catalog.client.model.ProductWithSkuDto;
import com.example.eshop.catalog.client.model.SkuInfoDto;
import com.example.eshop.catalog.config.AppProperties;
import com.example.eshop.catalog.domain.product.Product.ProductId;
import com.example.eshop.catalog.rest.api.ProductsApi;
import com.example.eshop.catalog.rest.mappers.ProductMapper;
import com.example.eshop.localizer.Localizer;
import com.example.eshop.rest.models.BasicErrorDto;
import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import java.util.Collections;
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
    private BasicErrorDto handleProductNotFoundException(ProductNotFoundException e) {
        return new BasicErrorDto(
                HttpStatus.NOT_FOUND.value(),
                getLocalizer().getMessage("productNotFound", e.getProductId())
        );
    }

    @Override
    public ResponseEntity<ProductWithSkuDto> getProductById(String id) {
        var product = productCrudService.getById(new ProductId(id));

        return ResponseEntity.ok(productMapper.toProductWithSkuDto(product));
    }

    @Override
    public ResponseEntity<PagedProductListDto> getProductList(Integer perPage, Integer page) {
        var pageable = PageRequest.of(page - 1, perPage);

        var products = productCrudService.getList(pageable);

        return ResponseEntity.ok(productMapper.toPagedProductListDto(products));
    }

    @Override
    public ResponseEntity<SkuInfoDto> getSku(List<Ean> eanList) {
        SkuInfoDto skuInfo;

        if (CollectionUtils.isEmpty(eanList)) {
            skuInfo = emptySkuList();
        } else {
            var products = productCrudService.getByEan(eanList, Pageable.unpaged());

            skuInfo = productMapper.toSkuList(eanList, products.getContent());
        }

        return ResponseEntity.ok(skuInfo);
    }

    /**
     * Returns new empty {@link SkuInfoDto}
     */
    private SkuInfoDto emptySkuList() {
        return new SkuInfoDto(Collections.emptyMap(), Collections.emptyList());
    }
}
