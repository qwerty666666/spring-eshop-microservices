package com.example.eshop.catalog.rest.controllers;

import com.example.eshop.catalog.application.services.categorycrudservice.CategoryCrudService;
import com.example.eshop.catalog.application.services.categorycrudservice.CategoryNotFoundException;
import com.example.eshop.catalog.application.services.productcrudservice.ProductCrudService;
import com.example.eshop.catalog.configs.ControllerTest;
import com.example.eshop.catalog.domain.category.Category;
import com.example.eshop.catalog.domain.category.Category.CategoryId;
import com.example.eshop.catalog.rest.mappers.CategoryMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoriesController.class)
@ControllerTest
class CategoriesControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private CategoryMapper categoryMapper;

    @MockBean
    private CategoryCrudService categoryCrudService;
    @MockBean
    private ProductCrudService productCrudService;

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
    class GetByIdTest {
        @Test
        void givenExistedCategoryId_whenGetByIdRequest_thenReturnOk() throws Exception {
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
        void givenNotExistedCategoryId_whenGetByIdRequest_thenReturn404() throws Exception {
            var id = new CategoryId("1");
            when(categoryCrudService.getCategory(id)).thenThrow(new CategoryNotFoundException(id, ""));

            assert404("/api/categories/" + id, id);

            verify(categoryCrudService).getCategory(id);
        }
    }

    @Nested
    class GetListTest {
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
    class GetTreeTest {
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
    class GetProductsTest {
        @Test
        void givenNotExistedCategoryId_whenGetProductsRequest_thenReturn404() throws Exception {
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
                            "status": 404,
                            "detail": "Category %s not found"
                        }
                        """.formatted(id)
                ));
    }
}
