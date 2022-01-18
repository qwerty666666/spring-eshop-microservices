package com.example.eshop.rest.controllers;

import com.example.eshop.catalog.application.services.categorycrudservice.CategoryCrudService;
import com.example.eshop.catalog.application.services.categorycrudservice.CategoryNotFoundException;
import com.example.eshop.catalog.application.services.productcrudservice.ProductCrudService;
import com.example.eshop.catalog.application.services.productcrudservice.ProductNotFoundException;
import com.example.eshop.catalog.domain.category.Category;
import com.example.eshop.catalog.domain.category.Category.CategoryId;
import com.example.eshop.catalog.domain.product.Product;
import com.example.eshop.catalog.domain.product.Product.ProductId;
import com.example.eshop.catalog.domain.product.Sku;
import com.example.eshop.rest.config.ControllerTestConfig;
import com.example.eshop.rest.mappers.CategoryMapper;
import com.example.eshop.rest.mappers.ProductMapper;
import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import com.example.eshop.sharedkernel.domain.valueobject.Money;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
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

@WebMvcTest(CatalogController.class)
@ActiveProfiles("test")
@Import(ControllerTestConfig.class)
class CatalogControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private CategoryMapper categoryMapper;

    @MockBean
    private CategoryCrudService categoryCrudService;
    @MockBean
    private ProductCrudService productCrudService;

    @Nested
    class ProductTests {
        @Nested
        class GetProductById {
            @Test
            void givenGetByIdRequest_whenProductExists_thenReturnOk() throws Exception {
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
            void givenGetByIdRequest_whenProductNotFound_thenReturn404() throws Exception {
                var product = createProduct();
                var id = product.getId();
                when(productCrudService.getById(id)).thenThrow(new ProductNotFoundException(id, ""));

                mockMvc.perform(get("/api/products/" + product.getId()))
                        .andExpect(status().isNotFound())
                        .andExpect(content().json("""
                                {
                                    status: 404,
                                    detail: "Product %s not found"
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

    @Nested
    class CategoryTests {
        private Category parent;
        private Category child1;
        private Category child2;

        @BeforeEach
        void setUp() {
            parent = Category.builder().id(new CategoryId("1")).name("parent").build();
            child1 = Category.builder().id(new CategoryId("2")).name("child1").parent(parent).build();
            child2 = Category.builder().id(new CategoryId("3")).name("child2").parent(parent).build();

            parent.addChild(child1);
            parent.addChild(child2);
        }

        @Nested
        class GetById {
            @Test
            void givenGetByIdRequest_whenCategoryExists_thenReturnOk() throws Exception {
                var category = parent;
                when(categoryCrudService.getCategory(category.getId())).thenReturn(category);

                var expectedJson = objectMapper.writeValueAsString(categoryMapper.toCategoryDto(category));

                mockMvc.perform(get("/api/categories/" + category.getId()))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(content().json(expectedJson));

                verify(categoryCrudService).getCategory(category.getId());
            }

            @Test
            void givenGetByIdRequest_whenCategoryDoesNotExist_thenReturn404() throws Exception {
                var id = new CategoryId("1");
                when(categoryCrudService.getCategory(id)).thenThrow(new CategoryNotFoundException(id, ""));

                assert404("/api/categories/" + id, id);

                verify(categoryCrudService).getCategory(id);
            }
        }

        @Nested
        class GetList {
            @Test
            void whenGetListRequest_thenReturnAllCategories() throws Exception {
                var categories = List.of(parent, child1, child2);
                when(categoryCrudService.getAll()).thenReturn(categories);

                var expectedJson = objectMapper.writeValueAsString(categoryMapper.toCategoryDtoList(categories));

                mockMvc.perform(get("/api/categories"))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(content().json(expectedJson));
            }
        }

        @Nested
        class GetTree {
            @Test
            void whenGetTreeRequest_thenReturnOk() throws Exception {
                var tree = List.of(parent);
                when(categoryCrudService.getTree()).thenReturn(tree);

                var expectedJson = objectMapper.writeValueAsString(categoryMapper.toTree(tree));

                mockMvc.perform(get("/api/categories/tree"))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(content().json(expectedJson));
            }
        }

        @Nested
        class GetProducts {
            @Test
            void givenGetProductsRequest_whenCategoryDoesNotExist_thenReturn404() throws Exception {
                var id = new CategoryId("1");
                var perPage = 5;
                var pageable = PageRequest.of(0, perPage);
                when(productCrudService.getByCategory(id, pageable)).thenThrow(new CategoryNotFoundException(id, ""));

                assert404("/api/categories/" + id + "/products/?per_page=" + perPage, id);

                verify(productCrudService).getByCategory(id, pageable);
            }
        }

        private void assert404(String url, CategoryId id) throws Exception {
            mockMvc.perform(get(url))
                    .andExpect(status().isNotFound())
                    .andExpect(content().json("""
                            {
                                status: 404,
                                detail: "Category %s not found"
                            }
                            """.formatted(id)
                    ));
        }
    }
}
