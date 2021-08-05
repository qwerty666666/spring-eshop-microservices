package com.example.eshop.rest.controllers;

import com.example.eshop.core.catalog.application.CategoryCrudService;
import com.example.eshop.core.catalog.application.exceptions.CategoryNotFoundException;
import com.example.eshop.core.catalog.application.ProductCrudService;
import com.example.eshop.core.catalog.domain.category.Category;
import com.example.eshop.core.catalog.domain.category.Category.CategoryId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
class CategoryControllerTest {
    @MockBean
    private CategoryCrudService categoryCrudService;
    @MockBean
    private ProductCrudService productCrudService;

    @Autowired
    private MockMvc mockMvc;

    //--------------------------
    // getById()
    //--------------------------

    @Test
    void givenGetByIdRequest_whenCategoryExists_thenReturnOk() throws Exception {
        var category = createCategory();
        when(categoryCrudService.getCategory(category.id())).thenReturn(category);

        mockMvc.perform(get("/api/categories/" + category.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(category.id().toString()));

        verify(categoryCrudService).getCategory(category.id());
    }

    @Test
    void givenGetByIdRequest_whenCategoryDoesNotExist_thenReturn404() throws Exception {
        var category = createCategory();
        var id = category.id();
        when(categoryCrudService.getCategory(id)).thenThrow(new CategoryNotFoundException(id, ""));

        assert404("/api/categories/" + id, id);

        verify(categoryCrudService).getCategory(id);
    }

    //--------------------------
    // getList()
    //--------------------------

    @Test
    void whenGetListRequest_thenReturnAllCategories() throws Exception {
        var categories = createCategoryList();
        when(categoryCrudService.getAll()).thenReturn(categories);

        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].id").value("11111111-1111-1111-1111-111111111111"))
                .andExpect(jsonPath("$[1].id").value("22222222-2222-2222-2222-222222222222"))
                .andExpect(jsonPath("$[2].id").value("33333333-3333-3333-3333-333333333333"));
    }

    //--------------------------
    // getProducts()
    //--------------------------

    @Test
    void givenGetProductsRequest_whenCategoryDoesNotExist_thenReturn404() throws Exception {
        var category = createCategory();
        var id = category.id();
        var pageable = PageRequest.of(0, CategoryController.PRODUCTS_DEFAULT_PAGE_SIZE);
        when(productCrudService.getForCategory(id, pageable)).thenThrow(new CategoryNotFoundException(id, ""));

        assert404("/api/categories/" + id + "/products", id);

        verify(productCrudService).getForCategory(id, pageable);
    }

    private Category createCategory() {
        return Category.builder()
                .id(new CategoryId(UUID.fromString("11111111-1111-1111-1111-111111111111")))
                .name("test")
                .build();
    }

    private List<Category> createCategoryList() {
        return List.of(
                Category.builder()
                        .id(new CategoryId(UUID.fromString("11111111-1111-1111-1111-111111111111")))
                        .name("test1")
                        .build(),
                Category.builder()
                        .id(new CategoryId(UUID.fromString("22222222-2222-2222-2222-222222222222")))
                        .name("test2")
                        .build(),
                Category.builder()
                        .id(new CategoryId(UUID.fromString("33333333-3333-3333-3333-333333333333")))
                        .name("test3").
                        build()
        );
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