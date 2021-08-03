package com.example.eshop.rest.controllers;

import com.example.eshop.core.catalog.application.CategoryCrudService;
import com.example.eshop.core.catalog.application.CategoryNotFoundException;
import com.example.eshop.core.catalog.domain.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
class CategoryControllerTest {
    @MockBean
    private CategoryCrudService categoryCrudService;

    @Autowired
    private MockMvc mockMvc;

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
    void givenGetByIdRequest_whenCategoryDoesNotExist_thenReturnNotFound() throws Exception {
        var category = createCategory();
        when(categoryCrudService.getCategory(category.id())).thenThrow(CategoryNotFoundException.class);

        mockMvc.perform(get("/api/categories/" + category.id()))
                .andExpect(status().isNotFound())
                .andExpect(content().json("""
                        {
                            status: 404,
                            detail: "Category %s not found"
                        }
                        """.formatted(category.id())
                ));

        verify(categoryCrudService).getCategory(category.id());
    }

    private Category createCategory() {
        return Category.builder()
                .id(UUID.fromString("11111111-1111-1111-1111-111111111111"))
                .name("test")
                .build();
    }
}