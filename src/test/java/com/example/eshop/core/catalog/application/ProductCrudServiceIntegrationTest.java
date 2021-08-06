package com.example.eshop.core.catalog.application;

import com.example.eshop.core.catalog.application.exceptions.CategoryNotFoundException;
import com.example.eshop.core.catalog.application.exceptions.ProductNotFoundException;
import com.example.eshop.core.catalog.domain.category.Category.CategoryId;
import com.example.eshop.core.catalog.domain.product.Product;
import com.example.eshop.core.catalog.domain.product.Product.ProductId;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.spring.api.DBRider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@DBRider
class ProductCrudServiceIntegrationTest {
    private static final ProductId SNEAKERS_PRODUCT_ID = new ProductId(1L);
    private static final ProductId SHIRT_PRODUCT_ID = new ProductId(2L);
    private static final ProductId JACKET_PRODUCT_ID = new ProductId(3L);
    private static final ProductId NON_EXISTENT_PRODUCT_ID = new ProductId(123L);

    private static final CategoryId CLOTHES_CATEGORY_ID = new CategoryId(1L);
    private static final CategoryId NON_EXISTENT_CATEGORY_ID = new CategoryId(123L);

    @Autowired
    ProductCrudService productCrudService;

    //--------------------------
    // getProduct()
    //--------------------------

    @Test
    @DataSet(value = "products.yml", cleanAfter = true)
    void getProduct_shouldFindProductById() {
        var product = productCrudService.getProduct(SNEAKERS_PRODUCT_ID);

        assertAll(
                () -> assertThat(SNEAKERS_PRODUCT_ID).as("Product Id").isEqualTo(product.id()),
                () -> assertThat("sneakers").as("Product Name").isEqualTo(product.getName())
        );
    }

    @Test
    @DataSet(value = "products.yml", cleanAfter = true)
    void getProduct_shouldThrowExceptionWhenProductNotFound() {
        assertThatThrownBy(() -> productCrudService.getProduct(NON_EXISTENT_PRODUCT_ID))
                .isInstanceOf(ProductNotFoundException.class);
    }

    //--------------------------
    // getList()
    //--------------------------

    @Test
    @DataSet(value = "products.yml", cleanAfter = true)
    void getList_shouldReturnProductListForTheGivenPage() {
        var size = 2;
        var pageable = PageRequest.ofSize(size)
                .first()
                .withSort(Sort.Direction.ASC, "id");

        var page = productCrudService.getList(pageable);

        assertAll(
                () -> assertThat(page.getContent().size()).as("Page size").isEqualTo(size),
                () -> assertThat(page.getTotalPages()).as("Total pages").isEqualTo(2),
                () -> assertThat(page.getTotalElements()).as("Total Elements").isEqualTo(3),
                () -> assertThat(page.getContent().get(0).id()).as("First Product").isEqualTo(SNEAKERS_PRODUCT_ID),
                () -> assertThat(page.getContent().get(1).id()).as("Second Product").isEqualTo(SHIRT_PRODUCT_ID)
        );
    }

    //--------------------------
    // getForCategory()
    //--------------------------

    @Test
    @DataSet(value = "products_categories.yml", cleanAfter = true)
    void getForCategory_shouldFindProductsForGivenCategory() {
        var pageable = PageRequest.of(0, 10).withSort(Direction.ASC, "id");

        var page = productCrudService.getForCategory(CLOTHES_CATEGORY_ID, pageable);

        assertAll(
                () -> assertThat(page.getContent().size()).as("Product count").isEqualTo(2),
                () -> assertThat(page.getContent()).as("Products found")
                        .extracting(Product::id)
                        .containsOnly(SHIRT_PRODUCT_ID, JACKET_PRODUCT_ID)
        );
    }

    @Test
    @DataSet(value = "products_categories.yml", cleanAfter = true)
    void getForCategory_shouldReturnProductForGivenPage() {
        var pageable = PageRequest.of(0, 1).withSort(Direction.ASC, "id");

        var page = productCrudService.getForCategory(CLOTHES_CATEGORY_ID, pageable);

        assertAll(
                () -> assertThat(page.getTotalElements()).as("Total elements").isEqualTo(2),
                () -> assertThat(page.getTotalPages()).as("Total pages").isEqualTo(2),
                () -> assertThat(page.getNumber()).as("Page number").isEqualTo(0),
                () -> assertThat(page.getNumberOfElements()).as("Page size").isEqualTo(1)
        );
    }

    @Test
    @DataSet(value = "products_categories.yml", cleanAfter = true)
    void getForCategory_shouldThrowExceptionWhenCategoryNotFound() {
        assertThatThrownBy(() -> productCrudService.getForCategory(NON_EXISTENT_CATEGORY_ID, PageRequest.ofSize(1)))
                .isInstanceOf(CategoryNotFoundException.class);
    }
}
