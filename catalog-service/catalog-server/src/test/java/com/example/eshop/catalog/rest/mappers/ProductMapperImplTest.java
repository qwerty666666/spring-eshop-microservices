package com.example.eshop.catalog.rest.mappers;

import com.example.eshop.catalog.client.api.model.AttributeDto;
import com.example.eshop.catalog.client.api.model.ImageDto;
import com.example.eshop.catalog.client.api.model.MoneyDto;
import com.example.eshop.catalog.client.api.model.ProductDto;
import com.example.eshop.catalog.client.api.model.ProductWithSkuDto;
import com.example.eshop.catalog.client.api.model.SkuDto;
import com.example.eshop.catalog.client.api.model.SkuInfoDto;
import com.example.eshop.catalog.configs.MappersTest;
import com.example.eshop.catalog.domain.file.File;
import com.example.eshop.catalog.domain.product.Attribute;
import com.example.eshop.catalog.domain.product.AttributeValue;
import com.example.eshop.catalog.domain.product.Product;
import com.example.eshop.catalog.domain.product.Product.ProductId;
import com.example.eshop.catalog.domain.product.Sku;
import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import com.example.eshop.sharedkernel.domain.valueobject.Money;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@MappersTest
class ProductMapperImplTest {
    private static final Ean SKU1_EAN = Ean.fromString("1111111111111");
    private static final Ean SKU2_EAN = Ean.fromString("2222222222222");

    @Autowired
    private ProductMapper mapper;

    @Test
    void testToSkuList() {
        // Given
        var eans = List.of(SKU1_EAN, SKU2_EAN);
        var product = createProduct();
        var sku = product.getSku().stream().filter(s -> eans.contains(s.getEan())).toList();

        // When
        var dto = mapper.toSkuList(eans, List.of(product));

        // Then
        assertSkuInfoEquals(sku, dto);
    }

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
    void testToProductWithSkuDto() {
        // Given
        var product = createProduct();

        // When
        var dto = mapper.toProductWithSkuDto(product);
        
        // Then
        assertProductWithSkuEquals(product, dto);
    }

    @Test
    void testToPagedProductListDto() {
        // Given
        var page = new PageImpl<>(List.of(createProduct()), PageRequest.of(1, 1), 4);

        // When
        var dto = mapper.toPagedProductListDto(page);

        // Then
        Assertions.assertPageableEquals(page, dto.getPageable());
        Assertions.assertListEquals(page.stream().toList(), dto.getItems(), this::assertProductWithSkuEquals);
    }

    private Product createProduct() {
        var product = Product.builder()
                .id(new ProductId("1"))
                .name("Sneakers")
                .description("description")
                .addImage(new File("img-1"))
                .addImage(new File("img-2"))
                .build();

        var sizeAttribute = new Attribute(1L, "size");
        var colorAttribute = new Attribute(2L, "color");

        product.addSku(Sku.builder()
                .ean(SKU1_EAN)
                .price(Money.USD(1))
                .availableQuantity(1)
                .addAttribute(new AttributeValue(sizeAttribute, "XXL", 2))
                .addAttribute(new AttributeValue(colorAttribute, "red", 1))
                .build()
        );
        product.addSku(Sku.builder()
                .ean(SKU2_EAN)
                .price(Money.USD(2))
                .availableQuantity(2)
                .addAttribute(new AttributeValue(sizeAttribute, "M", 1))
                .addAttribute(new AttributeValue(colorAttribute, "blue", 2))
                .build()
        );

        return product;
    }

    private void assertSkuInfoEquals(List<Sku> sku, SkuInfoDto skuInfo) {
        // assert SkuInfo::products
        var products = sku.stream()
                .map(Sku::getProduct)
                .collect(Collectors.toMap(
                        product -> product.getId().toString(),  // NOSONAR npe
                        Function.identity(),
                        (p1, p2) -> p1)
                );

        assertThat(skuInfo.getProducts()).containsOnlyKeys(products.keySet());
        skuInfo.getProducts().forEach((id, productDto) -> {
            assertProductEquals(products.get(id), productDto);
        });

        // assert SkuInfo::sku
        var skuMap = sku.stream()
                .collect(Collectors.toMap(
                        s -> s.getEan().toString(),
                        Function.identity())
                );

        assertThat(skuInfo.getSku()).hasSize(sku.size());
        skuInfo.getSku().forEach(skuDto -> {
            assertSkuEquals(skuMap.get(skuDto.getEan()), skuDto);
        });
    }

    private void assertProductEquals(Product product, ProductDto productDto) {
        assertThat(productDto.getId()).as("product ID")
                .isEqualTo(product.getId() == null ? null : product.getId().toString());
        assertThat(productDto.getName()).as("product Name").isEqualTo(product.getName());
        assertThat(productDto.getDescription()).as("product Description").isEqualTo(product.getDescription());
        assertImageEquals(product.getImages(), productDto.getImages());
    }

    private void assertProductWithSkuEquals(Product product, ProductWithSkuDto productDto) {
        assertThat(productDto.getId()).as("product ID")
                .isEqualTo(product.getId() == null ? null : product.getId().toString());
        assertThat(productDto.getName()).as("product Name").isEqualTo(product.getName());
        assertThat(productDto.getDescription()).as("product Description").isEqualTo(product.getDescription());
        Assertions.assertListEquals(product.getSku(), productDto.getSku(), this::assertSkuEquals);
        assertImageEquals(product.getImages(), productDto.getImages());
    }

    private void assertSkuEquals(Sku sku, SkuDto skuDto) {
        assertThat(skuDto.getEan()).as("Sku EAN").isEqualTo(sku.getEan().toString());
        assertThat(skuDto.getQuantity()).as("available quantity").isEqualTo(sku.getAvailableQuantity());
        assertPriceEquals(sku.getPrice(), skuDto.getPrice());
        Assertions.assertListEquals(sku.getAttributeValues(), skuDto.getAttributes(), ProductMapperImplTest::assertAttributeEquals);
    }

    private static void assertPriceEquals(Money money, MoneyDto moneyDto) {
        assertThat(moneyDto.getAmount()).as("price amount").isEqualTo(money.getAmount());
        assertThat(moneyDto.getCurrency()).as("price currency").isEqualTo(money.getCurrency().getCurrencyCode());
    }

    private static void assertImageEquals(List<File> images, List<ImageDto> imageDtos) {
        // check only collection size because we don't know what URL will be used in imageDto,
        // as image can be external URL or can be prefixed with hostname by UriBuilder
        assertThat(images).hasSize(imageDtos.size());
    }

    private static void assertAttributeEquals(AttributeValue attributeValue, AttributeDto attributeDto) {
        assertThat(attributeDto.getId()).as("Attribute ID")
                .isEqualTo(attributeValue.getId() == null ? null : attributeValue.getId().toString());
        assertThat(attributeDto.getName()).as("Attribute Name").isEqualTo(attributeValue.getAttribute().getName());
        assertThat(attributeDto.getValue()).as("Attribute Value").isEqualTo(attributeValue.getValue());
    }
}
