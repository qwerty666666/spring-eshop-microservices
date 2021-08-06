package com.example.eshop.core.catalog.application;

import com.example.eshop.core.catalog.application.exceptions.CategoryNotFoundException;
import com.example.eshop.core.catalog.domain.category.Category.CategoryId;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.spring.api.DBRider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@DBRider
class CategoryCrudServiceIntegrationTest {
    private static final CategoryId PARENT_CATEGORY_ID = new CategoryId(1L);
    private static final CategoryId NOT_EXISTING_CATEGORY_ID = new CategoryId(123L);

    @Autowired
    CategoryCrudService categoryCrudService;

    @Test
    @DataSet("categories.yml")
    void shouldFindCategoryById() {
        var category = categoryCrudService.getCategory(PARENT_CATEGORY_ID);

        assertAll(
                () -> assertThat(category.id()).isEqualTo(PARENT_CATEGORY_ID),
                () -> assertThat(category.getName()).isEqualTo("parent")
        );
    }

    @Test
    @DataSet("categories.yml")
    void shouldThrowExceptionWhenCategoryDoesNotExist() {
        assertThatThrownBy(() -> categoryCrudService.getCategory(NOT_EXISTING_CATEGORY_ID))
                .isInstanceOf(CategoryNotFoundException.class);
    }

    @Test
    @DataSet("categories.yml")
    void shouldFindAllCategories() {
        var categories = categoryCrudService.getAll();

        assertThat(categories).hasSize(4);
    }

    @Test
    @DataSet("categories.yml")
    void shouldReturnRootNodesForCategoryTree() {
        var tree = categoryCrudService.getTree();

        assertAll(
                () -> assertThat(tree).as("Root nodes count").hasSize(1),
                () -> assertThat(tree.get(0).id()).as("Root Category ids").isEqualTo(PARENT_CATEGORY_ID)
        );
    }
}
