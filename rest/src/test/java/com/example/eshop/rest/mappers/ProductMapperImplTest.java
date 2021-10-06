package com.example.eshop.rest.mappers;

import com.example.eshop.catalog.domain.file.File;
import com.example.eshop.catalog.domain.product.Attribute;
import com.example.eshop.catalog.domain.product.AttributeValue;
import com.example.eshop.catalog.domain.product.Product;
import com.example.eshop.catalog.domain.product.Product.ProductId;
import com.example.eshop.catalog.domain.product.Sku;
import com.example.eshop.rest.config.MappersConfig;
import com.example.eshop.rest.dto.AttributeDto;
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
        Utils.assertListEquals(page.stream().toList(), dto.getItems(), this::assertProductEquals);
    }

    private Product createProduct() {
        var product = Product.builder()
                .id(new ProductId("1"))
                .name("Sneakers")
                .addImage(new File("img-1"))
                .addImage(new File("img-2"))
                .build();

        var sizeAttribute = new Attribute(1L, "size");
        var colorAttribute = new Attribute(2L, "color");

        product.addSku(Sku.builder()
                .ean(Ean.fromString("1111111111111"))
                .price(Money.USD(1))
                .availableQuantity(1)
                .addAttribute(new AttributeValue(sizeAttribute, "XXL", 2))
                .addAttribute(new AttributeValue(colorAttribute, "red", 1))
                .build()
        );
        product.addSku(Sku.builder()
                .ean(Ean.fromString("2222222222222"))
                .price(Money.USD(2))
                .availableQuantity(2)
                .addAttribute(new AttributeValue(sizeAttribute, "M", 1))
                .addAttribute(new AttributeValue(colorAttribute, "blue", 2))
                .build()
        );

        return product;
    }

    private void assertProductEquals(Product product, ProductDto productDto) {
        assertThat(productDto.getId()).as("product ID")
                .isEqualTo(product.getId() == null ? null : product.getId().toString());
        assertThat(productDto.getName()).as("product Name").isEqualTo(product.getName());
        Utils.assertListEquals(product.getSku(), productDto.getSku(), this::assertSkuEquals);
        // check only collection size because we don't know what URL will be used in imageDto
        assertThat(productDto.getImages()).as("images").hasSize(product.getImages().size());
    }

    private void assertSkuEquals(Sku sku, SkuDto skuDto) {
        assertThat(skuDto.getEan()).as("Sku EAN").isEqualTo(sku.getEan().toString());
        assertThat(skuDto.getQuantity()).as("available quantity").isEqualTo(sku.getAvailableQuantity());
        Utils.assertPriceEquals(sku.getPrice(), skuDto.getPrice());
        Utils.assertListEquals(sku.getAttributeValues(), skuDto.getAttributes(), this::assertAttributeEquals);
    }

    private void assertAttributeEquals(AttributeValue attributeValue, AttributeDto attributeDto) {
        assertThat(attributeDto.getId()).as("Attribute ID")
                .isEqualTo(attributeValue.getId() == null ? null : attributeValue.getId().toString());
        assertThat(attributeDto.getName()).as("Attribute Name").isEqualTo(attributeValue.getAttribute().getName());
        assertThat(attributeDto.getValue()).as("Attribute Value").isEqualTo(attributeValue.getValue());
    }
}
