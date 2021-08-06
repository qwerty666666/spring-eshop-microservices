package com.example.eshop.rest.controllers;

import com.example.eshop.core.catalog.application.ProductCrudService;
import com.example.eshop.core.catalog.application.exceptions.ProductNotFoundException;
import com.example.eshop.core.catalog.domain.product.Product;
import com.example.eshop.core.catalog.domain.product.Product.ProductId;
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
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {
    @MockBean
    ProductCrudService productCrudService;

    @Autowired
    private MockMvc mockMvc;

    //--------------------------
    // getById()
    //--------------------------

    @Test
    void givenGetByIdRequest_whenProductExists_thenReturnOk() throws Exception {
        var product = createProduct();
        when(productCrudService.getProduct(product.id())).thenReturn(product);

        mockMvc.perform(get("/api/products/" + product.id()))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString()))
                .andExpect(jsonPath("$.id").value(product.id().toString()));

        verify(productCrudService).getProduct(product.id());
    }

    @Test
    void givenGetByIdRequest_whenProductNotFound_thenReturn404() throws Exception {
        var product = createProduct();
        var id = product.id();
        when(productCrudService.getProduct(id)).thenThrow(new ProductNotFoundException(id, ""));

        mockMvc.perform(get("/api/products/" + product.id()))
                .andExpect(status().isNotFound())
                .andExpect(content().json("""
                        {
                            status: 404,
                            detail: "Product %s not found"
                        }""".formatted(product.id())
                ));

        verify(productCrudService).getProduct(id);
    }

    private Product createProduct() {
        return Product.builder()
                .id(new ProductId(1L))
                .name("test")
                .build();
    }

    //--------------------------
    // getList()
    //--------------------------

    @Test
    void givenGetListRequest_whenPageableRequested_thenReturnListForThisPage() throws Exception {
        var productList = createProductList();
        var pageable = Pageable.ofSize(2).withPage(0);
        var resultPage = new PageImpl<>(productList.subList(0, 2), pageable, 3);
        when(productCrudService.getList(pageable)).thenReturn(resultPage);

        mockMvc.perform(get("/api/products/?page=1&per_page=2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page", is(1)))
                .andExpect(jsonPath("$.perPage", is(2)))
                .andExpect(jsonPath("$.totalItems", is(3)))
                .andExpect(jsonPath("$.totalPages", is(2)))
                .andExpect(jsonPath("$.items", hasSize(2)))
                .andExpect(jsonPath("$.items[0].id", is("1")))
                .andExpect(jsonPath("$.items[1].id", is("2")));

        verify(productCrudService).getList(pageable);
    }

    @Test
    void givenGetListRequest_whenRequestWithoutPageableParameter_thenReturnFirstPage() throws Exception {
        Page<Product> page = new PageImpl<>(Collections.emptyList());
        when(productCrudService.getList(any())).thenReturn(page);

        mockMvc.perform(get("/api/products/"));

        verify(productCrudService).getList(Pageable.ofSize(ProductController.DEFAULT_PAGE_SIZE).withPage(0));
    }

    @Test
    void givenGetListRequest_whenPageSizeRequestParameterGreaterThanMaxAllowed_thenRestrictSizeToMax() throws Exception {
        Page<Product> page = new PageImpl<>(Collections.emptyList());
        when(productCrudService.getList(any())).thenReturn(page);

        mockMvc.perform(get("/api/products/?per_page=" + (ProductController.MAX_PAGE_SIZE + 10)));

        verify(productCrudService).getList(Pageable.ofSize(ProductController.MAX_PAGE_SIZE).withPage(0));
    }

    private List<Product> createProductList() {
        return List.of(
                Product.builder().id(new ProductId(1L)).build(),
                Product.builder().id(new ProductId(2L)).build(),
                Product.builder().id(new ProductId(3L)).build()
        );
    }
}
