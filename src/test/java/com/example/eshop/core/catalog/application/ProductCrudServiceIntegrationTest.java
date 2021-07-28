package com.example.eshop.core.catalog.application;

import com.example.eshop.core.catalog.domain.Product.ProductId;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.spring.api.DBRider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@DBRider
class ProductCrudServiceIntegrationTest {
    private static final ProductId SNEAKERS_PRODUCT_ID = new ProductId(
            UUID.fromString("11111111-1111-1111-1111-111111111111")
    );
    private static final ProductId SHIRT_PRODUCT_ID = new ProductId(
            UUID.fromString("22222222-2222-2222-2222-222222222222")
    );
    private static final ProductId NON_EXISTING_PRODUCT_ID = new ProductId(
            UUID.fromString("12345678-1234-1234-1234-123456789012")
    );

    @Autowired
    ProductCrudService productCrudService;

    @Test
    @DataSet("products.yml")
    void shouldFindProductById() {
        var product = productCrudService.getProduct(SNEAKERS_PRODUCT_ID);

        assertAll(
                () -> assertThat(SNEAKERS_PRODUCT_ID).as("Product Id").isEqualTo(product.id()),
                () -> assertThat("sneakers").as("Product Name").isEqualTo(product.getName())
        );
    }

    @Test
    @DataSet("products.yml")
    void shouldThrowExceptionWhenProductNotFound() {
        assertThatThrownBy(() -> productCrudService.getProduct(NON_EXISTING_PRODUCT_ID))
                .isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    @DataSet("products.yml")
    void shouldReturnProductListForTheGivenPage() {
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
}
