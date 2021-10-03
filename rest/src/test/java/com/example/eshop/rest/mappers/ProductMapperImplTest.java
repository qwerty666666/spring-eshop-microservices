package com.example.eshop.rest.mappers;

import com.example.eshop.catalog.domain.product.Product;
import com.example.eshop.catalog.domain.product.Product.ProductId;
import com.example.eshop.catalog.domain.product.Sku;
import com.example.eshop.rest.config.MappersConfig;
import com.example.eshop.rest.dto.ProductDto;
import com.example.eshop.rest.dto.SkuDto;
import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import com.example.eshop.sharedkernel.domain.valueobject.Money;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = MappersConfig.class)
class ProductMapperImplTest {
    @Autowired
    ProductMapper mapper;

    @Test
    void testToProductDto() {
        // Given
        var product = createProduct();

        // When
        var dto = mapper.toProductDto(product);
        
        // Then
        assertProductEquals(product, dto);
    }

    @Test
    void testToPagedProductListDto() {
        // Given
        var page = new PageImpl<>(List.of(createProduct()), PageRequest.of(1, 1), 4);

        // When
        var dto = mapper.toPagedProductListDto(page);

        // Then
        Utils.assertPageableEquals(page, dto.getPageable());
        Utils.assertListTheSame(page.stream().toList(), dto.getItems(), ProductMapperImplTest::assertProductEquals);
    }

    private Product createProduct() {
        var product = Product.builder()
                .id(new ProductId("1"))
                .name("Sneakers")
                .build();

        product.addSku(Sku.builder()
                .ean(Ean.fromString("1111111111111"))
                .price(Money.USD(1))
                .availableQuantity(1)
                .build()
        );
        product.addSku(Sku.builder()
                .ean(Ean.fromString("2222222222222"))
                .price(Money.USD(2))
                .availableQuantity(2)
                .build()
        );

        return product;
    }

    private static void assertProductEquals(Product product, ProductDto productDto) {
        assertThat(productDto.getId()).as("product ID").isEqualTo(product.getId().toString());
        assertThat(productDto.getName()).as("product Name").isEqualTo(product.getName());
        Utils.assertListTheSame(product.getSku(), productDto.getSku(), ProductMapperImplTest::assertSkuEquals);
    }

    private static void assertSkuEquals(Sku sku, SkuDto skuDto) {
        assertThat(skuDto.getEan()).as("Sku EAN").isEqualTo(sku.getEan().toString());
        assertThat(skuDto.getQuantity()).as("available quantity").isEqualTo(sku.getAvailableQuantity());
        Utils.assertPriceEquals(sku.getPrice(), skuDto.getPrice());
    }


}
