package com.example.eshop.rest.controllers;

import com.example.eshop.catalog.application.product.ProductCrudService;
import com.example.eshop.catalog.application.product.ProductNotFoundException;
import com.example.eshop.catalog.domain.product.Product;
import com.example.eshop.catalog.domain.product.Product.ProductId;
import com.example.eshop.rest.config.MappersConfig;
import com.example.eshop.rest.mappers.ProductMapper;
import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import com.example.eshop.sharedkernel.domain.valueobject.Money;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
@ActiveProfiles("test")
@Import(MappersConfig.class)
class ProductControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ProductMapper productMapper;

    @MockBean
    ProductCrudService productCrudService;

    @Nested
    class GetProductById {
        @Test
        void givenGetByIdRequest_whenProductExists_thenReturnOk() throws Exception {
            var product = createProduct();
            when(productCrudService.getProduct(product.getId())).thenReturn(product);

            var expectedJson = objectMapper.writeValueAsString(productMapper.toProductDto(product));

            mockMvc.perform(get("/api/products/" + product.getId()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString()))
                    .andExpect(content().json(expectedJson));

            verify(productCrudService).getProduct(product.getId());
        }

        @Test
        void givenGetByIdRequest_whenProductNotFound_thenReturn404() throws Exception {
            var product = createProduct();
            var id = product.getId();
            when(productCrudService.getProduct(id)).thenThrow(new ProductNotFoundException(id, ""));

            mockMvc.perform(get("/api/products/" + product.getId()))
                    .andExpect(status().isNotFound())
                    .andExpect(content().json("""
                            {
                                status: 404,
                                detail: "Product %s not found"
                            }""".formatted(product.getId())
                    ));

            verify(productCrudService).getProduct(id);
        }

        private Product createProduct() {
            var product = Product.builder().id(new ProductId("1")).name("test").build();
            product.addSku(Ean.fromString("1111111111111"), Money.USD(10), 12);

            return product;
        }
    }

    @Nested
    class GetProductList {
        @Test
        void givenGetListRequest_whenPageableRequested_thenReturnListForThisPage() throws Exception {
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
        void givenGetListRequest_whenRequestWithoutPageableParameter_thenReturnFirstPage() throws Exception {
            Page<Product> page = new PageImpl<>(Collections.emptyList());
            when(productCrudService.getList(any())).thenReturn(page);

            mockMvc.perform(get("/api/products/"));

            verify(productCrudService).getList(Pageable.ofSize(30).withPage(0));
        }

        @Test
        void givenGetListRequest_whenPageSizeInvalid_thenReturn400() throws Exception {
            Page<Product> page = new PageImpl<>(Collections.emptyList());
            when(productCrudService.getList(any())).thenReturn(page);

            mockMvc.perform(get("/api/products/?per_page=0"))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }

        private List<Product> createProductList() {
            var product1 = Product.builder().id(new ProductId("1")).name("test").build();
            product1.addSku(Ean.fromString("1111111111111"), Money.USD(10), 12);
            product1.addSku(Ean.fromString("2222222222222"), Money.USD(10), 15);

            var product2 = Product.builder().id(new ProductId("2")).name("test").build();
            product2.addSku(Ean.fromString("3333333333333"), Money.USD(10.15), 1);

            var product3 = Product.builder().id(new ProductId("3")).name("test").build();

            return List.of(product1, product2, product3);
        }
    }
}
