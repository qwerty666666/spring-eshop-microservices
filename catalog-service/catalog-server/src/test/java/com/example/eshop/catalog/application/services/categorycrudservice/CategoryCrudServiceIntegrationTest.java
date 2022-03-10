package com.example.eshop.catalog.application.services.categorycrudservice;

import com.example.eshop.catalog.configs.ExcludeKafkaConfig;
import com.example.eshop.catalog.domain.category.Category;
import com.example.eshop.catalog.domain.category.Category.CategoryId;
import com.example.eshop.sharedtest.IntegrationTest;
import com.example.eshop.sharedtest.dbtests.DbTest;
import com.github.database.rider.core.api.dataset.DataSet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@ActiveProfiles("test")
@IntegrationTest
@ExcludeKafkaConfig
@DbTest
class CategoryCrudServiceIntegrationTest {

    // These constants taken from DB datasets

    private static final CategoryId PARENT_CATEGORY_ID = new CategoryId("1");
    private static final CategoryId CHILD_1_CATEGORY_ID = new CategoryId("2");
    private static final CategoryId CHILD_2_CATEGORY_ID = new CategoryId("3");
    private static final CategoryId NOT_EXISTING_CATEGORY_ID = new CategoryId("123");

    @Autowired
    private CategoryCrudService categoryCrudService;

    @Test
    @DataSet("categories.yml")
    void givenCategoryId_whenGetCategory_thenReturnCategoryByGivenId() {
        var category = categoryCrudService.getCategory(PARENT_CATEGORY_ID);

        assertAll(
                () -> assertThat(category.getId()).isEqualTo(PARENT_CATEGORY_ID),
                () -> assertThat(category.getName()).isEqualTo("parent")
        );
    }

    @Test
    @DataSet("categories.yml")
    void givenNonExistingCategoryId_whenGetCategory_thenThrowCategoryNotFoundException() {
        assertThatThrownBy(() -> categoryCrudService.getCategory(NOT_EXISTING_CATEGORY_ID))
                .isInstanceOf(CategoryNotFoundException.class);
    }

    @Test
    @DataSet("categories.yml")
    void whenGetAll_thenReturnAllCategories() {
        var categories = categoryCrudService.getAll();

        assertThat(categories).hasSize(4);
    }

    @Test
    @DataSet("categories.yml")
    void whenGetTree_thenReturnRootNodesForCategoryTree() {
        var tree = categoryCrudService.getTree();

        assertAll(
                () -> assertThat(tree).as("Root nodes count").hasSize(1),
                () -> assertThat(tree.get(0).getId()).as("Root Category id").isEqualTo(PARENT_CATEGORY_ID),
                () -> assertThat(tree.get(0).getChildren()).as("Root children")
                        .hasSize(2)
                        .extracting(Category::getId)
                        .containsOnly(CHILD_1_CATEGORY_ID, CHILD_2_CATEGORY_ID)
        );
    }
}
