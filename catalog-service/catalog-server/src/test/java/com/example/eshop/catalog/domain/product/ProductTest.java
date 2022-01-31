package com.example.eshop.catalog.domain.product;

import com.example.eshop.catalog.domain.product.Product.ProductId;
import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import com.example.eshop.sharedkernel.domain.valueobject.Money;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatNoException;

class ProductTest {
    @Test
    void whenAddSkuWithDifferentAttributeList_thenThrowIllegalArgumentException() {
        // Given
        var attribute1 = new Attribute(1L, "attr 1");
        var attribute2 = new Attribute(2L, "attr 2");

        var product = new Product(new ProductId("1"));
        product.addSku(createSkuWithAttributes(List.of(attribute1)));

        // When + Then
        assertThatNoException().isThrownBy(() -> {
            product.addSku(createSkuWithAttributes(List.of(attribute1)));
        });

        assertThatIllegalArgumentException().isThrownBy(() -> {
            product.addSku(createSkuWithAttributes(List.of(attribute2)));
        });
    }

    private Sku createSkuWithAttributes(List<Attribute> attributes) {
        return Sku.builder()
                .ean(Ean.fromString("1111111111111"))
                .price(Money.USD(1))
                .attributes(attributes.stream().map(attr -> new AttributeValue(attr, "1", 1)).toList())
                .build();
    }
}