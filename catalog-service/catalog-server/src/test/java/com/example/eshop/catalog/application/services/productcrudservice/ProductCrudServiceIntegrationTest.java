package com.example.eshop.catalog.application.services.productcrudservice;

import com.example.eshop.catalog.application.services.categorycrudservice.CategoryNotFoundException;
import com.example.eshop.catalog.domain.category.Category.CategoryId;
import com.example.eshop.catalog.domain.product.Product;
import com.example.eshop.catalog.domain.product.Product.ProductId;
import com.example.eshop.sharedtest.dbtests.DbTest;
import com.github.database.rider.core.api.dataset.DataSet;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@DbTest
class ProductCrudServiceIntegrationTest {
    private static final ProductId SNEAKERS_PRODUCT_ID = new ProductId("1");
    private static final ProductId SHIRT_PRODUCT_ID = new ProductId("2");
    private static final ProductId JACKET_PRODUCT_ID = new ProductId("3");
    private static final ProductId NON_EXISTENT_PRODUCT_ID = new ProductId("non_existed");

    private static final CategoryId CLOTHES_CATEGORY_ID = new CategoryId("1");
    private static final CategoryId NON_EXISTENT_CATEGORY_ID = new CategoryId("non_existed");

    @Autowired
    private ProductCrudService productCrudService;

    @Nested
    class getById {
        @Test
        @DataSet(value = "products.yml", cleanAfter = true)
        void givenProductId_whenGetProduct_thenReturnProductById() {
            var product = productCrudService.getById(SNEAKERS_PRODUCT_ID);

            assertThat(product.getId()).as("Product Id").isEqualTo(SNEAKERS_PRODUCT_ID);
        }

        @Test
        @DataSet(value = "products.yml", cleanAfter = true)
        void givenNonExistingProductId_whenGetProduct_thenThrowProductNotFoundException() {
            assertThatThrownBy(() -> productCrudService.getById(NON_EXISTENT_PRODUCT_ID))
                    .isInstanceOf(ProductNotFoundException.class);
        }
    }

    @Nested
    class GetList {
        @Test
        @DataSet(value = "products.yml", cleanAfter = true)
        void givenPageable_whenGetList_thenReturnOnlyProductsForTheGivenPage() {
            var size = 2;
            var pageable = PageRequest.ofSize(size)
                    .first()
                    .withSort(Sort.Direction.ASC, "id");

            var page = productCrudService.getList(pageable);

            assertAll(
                    () -> assertThat(page.getContent().size()).as("Page size").isEqualTo(size),
                    () -> assertThat(page.getTotalPages()).as("Total pages").isEqualTo(2),
                    () -> assertThat(page.getTotalElements()).as("Total Elements").isEqualTo(3),
                    () -> assertThat(page.getContent().get(0).getId()).as("First Product").isEqualTo(SNEAKERS_PRODUCT_ID),
                    () -> assertThat(page.getContent().get(1).getId()).as("Second Product").isEqualTo(SHIRT_PRODUCT_ID)
            );
        }
    }

    @Nested
    class GetByCategory {
        @Test
        @DataSet(value = "products_categories.yml", cleanAfter = true)
        void givenCategoryId_whenGetForCategory_thenReturnProductsOnlyFromTheGivenCategory() {
            var pageable = PageRequest.of(0, 10).withSort(Direction.ASC, "id");

            var page = productCrudService.getByCategory(CLOTHES_CATEGORY_ID, pageable);

            assertAll(
                    () -> assertThat(page.getContent().size()).as("Product count").isEqualTo(2),
                    () -> assertThat(page.getContent()).as("Products found")
                            .extracting(Product::getId)
                            .containsOnly(SHIRT_PRODUCT_ID, JACKET_PRODUCT_ID)
            );
        }

        @Test
        @DataSet(value = "products_categories.yml", cleanAfter = true)
        void givenPageable_whenGetForCategory_thenReturnProductsOnlyForGivenPageAndFromGivenCategory() {
            var pageable = PageRequest.of(0, 1).withSort(Direction.ASC, "id");

            var page = productCrudService.getByCategory(CLOTHES_CATEGORY_ID, pageable);

            assertAll(
                    () -> assertThat(page.getTotalElements()).as("Total elements").isEqualTo(2),
                    () -> assertThat(page.getTotalPages()).as("Total pages").isEqualTo(2),
                    () -> assertThat(page.getNumber()).as("Page number").isZero(),
                    () -> assertThat(page.getNumberOfElements()).as("Page size").isEqualTo(1)
            );
        }

        @Test
        @DataSet(value = "products_categories.yml", cleanAfter = true)
        void givenNonExistingCategoryId_whenGetForCategory_thenThrowCategoryNotFoundException() {
            var pageable = PageRequest.ofSize(1);

            assertThatThrownBy(() -> productCrudService.getByCategory(NON_EXISTENT_CATEGORY_ID, pageable))
                    .isInstanceOf(CategoryNotFoundException.class);
        }
    }
}
