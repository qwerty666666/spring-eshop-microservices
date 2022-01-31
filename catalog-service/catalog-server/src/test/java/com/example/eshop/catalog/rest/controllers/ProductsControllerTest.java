package com.example.eshop.catalog.rest.controllers;

import com.example.eshop.catalog.configs.ExcludeKafkaConfig;
import com.example.eshop.catalog.application.services.productcrudservice.ProductCrudService;
import com.example.eshop.catalog.application.services.productcrudservice.ProductNotFoundException;
import com.example.eshop.catalog.domain.product.Product;
import com.example.eshop.catalog.domain.product.Product.ProductId;
import com.example.eshop.catalog.domain.product.Sku;
import com.example.eshop.catalog.configs.ControllerTest;
import com.example.eshop.catalog.rest.mappers.ProductMapper;
import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import com.example.eshop.sharedkernel.domain.valueobject.Money;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductsController.class)
@ExcludeKafkaConfig
@ControllerTest
class ProductsControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ProductMapper productMapper;

    @MockBean
    private ProductCrudService productCrudService;

    @Nested
    class GetProductByIdTest {
        @Test
        void givenExistedProductId_whenGetByIdRequest_thenReturnOk() throws Exception {
            var product = createProduct();
            when(productCrudService.getById(product.getId())).thenReturn(product);

            var expectedJson = objectMapper.writeValueAsString(productMapper.toProductDto(product));

            mockMvc.perform(get("/api/products/" + product.getId()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString()))
                    .andExpect(content().json(expectedJson));

            verify(productCrudService).getById(product.getId());
        }

        @Test
        void givenNotExistedProductId_whenGetByIdRequest_thenReturn404() throws Exception {
            var product = createProduct();
            var id = product.getId();
            when(productCrudService.getById(id)).thenThrow(new ProductNotFoundException(id, ""));

            mockMvc.perform(get("/api/products/" + product.getId()))
                    .andExpect(status().isNotFound())
                    .andExpect(content().json("""
                            {
                                "status": 404,
                                "detail": "Product %s not found"
                            }""".formatted(product.getId())
                    ));

            verify(productCrudService).getById(id);
        }

        private Product createProduct() {
            var product = Product.builder().id(new ProductId("1")).name("test").build();
            product.addSku(Sku.builder()
                    .ean(Ean.fromString("1111111111111"))
                    .price(Money.USD(10))
                    .availableQuantity(12)
                    .build()
            );

            return product;
        }
    }

    @Nested
    class GetProductListTest {
        @Test
        void givenPageable_whenGetListRequest_thenReturnListForThisPage() throws Exception {
            var productList = createProductList();
            var pageable = Pageable.ofSize(2).withPage(0);

            var resultPage = new PageImpl<>(productList.subList(0, 2), pageable, 3);
            when(productCrudService.getList(pageable)).thenReturn(resultPage);

            var expectedDto = productMapper.toPagedProductListDto(resultPage);
            var expectedJson = objectMapper.writeValueAsString(expectedDto);

            mockMvc.perform(get("/api/products/?page=1&per_page=2"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().json(expectedJson));

            verify(productCrudService).getList(pageable);
        }

        @Test
        void givenRequestWithoutPageableParameter_whenGetListRequest_thenReturnFirstPage() throws Exception {
            Page<Product> page = new PageImpl<>(Collections.emptyList());
            when(productCrudService.getList(any())).thenReturn(page);

            mockMvc.perform(get("/api/products/"));

            verify(productCrudService).getList(Pageable.ofSize(30).withPage(0));
        }

        @Test
        void givenInvalidPageSize_whenGetListRequest_thenReturn400() throws Exception {
            mockMvc.perform(get("/api/products/?per_page=0"))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors[?(@.field == 'perPage')]").exists());
        }

        @Test
        void givenEanQueryParameter_whenGetListRequest_thenReturnProductsFilteredByGivenEan() throws Exception {
            var productList = createProductList();
            var pageable = Pageable.ofSize(2).withPage(0);
            var ean = List.of(Ean.fromString("1111111111111"), Ean.fromString("2222222222222"));

            var resultPage = new PageImpl<>(productList.subList(0, 2), pageable, 3);
            when(productCrudService.getByEan(ean, pageable)).thenReturn(resultPage);

            var expectedDto = productMapper.toPagedProductListDto(resultPage);
            var expectedJson = objectMapper.writeValueAsString(expectedDto);

            mockMvc.perform(get("/api/products/?page=1&per_page=2&ean=1111111111111,2222222222222"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().json(expectedJson));

            verify(productCrudService).getByEan(ean, pageable);
        }

        @Test
        void givenInvalidEanQueryParameter_whenGetListRequest_thenReturn400() throws Exception {
            mockMvc.perform(get("/api/products/?page=1&per_page=2&ean=invalidEan"))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors[?(@.field == 'ean')]").exists());
        }

        private List<Product> createProductList() {
            var product1 = Product.builder().id(new ProductId("1")).name("test").build();
            product1.addSku(Sku.builder()
                    .ean(Ean.fromString("1111111111111"))
                    .price(Money.USD(10))
                    .availableQuantity(12)
                    .build()
            );
            product1.addSku(Sku.builder()
                    .ean(Ean.fromString("2222222222222"))
                    .price(Money.USD(10))
                    .availableQuantity(15)
                    .build()
            );

            var product2 = Product.builder().id(new ProductId("2")).name("test").build();
            product2.addSku(Sku.builder()
                    .ean(Ean.fromString("3333333333333"))
                    .price(Money.USD(10.15))
                    .availableQuantity(1)
                    .build()
            );

            var product3 = Product.builder().id(new ProductId("3")).name("test").build();

            return List.of(product1, product2, product3);
        }
    }
}
