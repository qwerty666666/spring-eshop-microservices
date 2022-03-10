package com.example.eshop.cart.rest.mappers;

import com.example.eshop.cart.config.MapperTest;
import com.example.eshop.cart.domain.Cart;
import com.example.eshop.catalog.client.CatalogService;
import com.example.eshop.catalog.client.SkuWithProductDto;
import com.example.eshop.catalog.client.api.model.AttributeDto;
import com.example.eshop.catalog.client.api.model.ImageDto;
import com.example.eshop.catalog.client.api.model.MoneyDto;
import com.example.eshop.catalog.client.api.model.ProductDto;
import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import com.example.eshop.sharedkernel.domain.valueobject.Money;
import com.example.eshop.sharedtest.ArgMatchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import reactor.core.publisher.Mono;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.when;

@MapperTest
class CartMapperImplTest {
    @MockBean
    private CatalogService catalogService;

    @Autowired
    private CartMapper mapper;

    @Test
    void testToCartDto() {
        // Given
        var ean1 = Ean.fromString("1111111111111");
        var ean2 = Ean.fromString("2222222222222");

        var price1 = Money.USD(10);
        var price2 = Money.USD(20);

        var availableQuantity1 = 10;
        var availableQuantity2 = 20;

        var product = ProductDto.builder()
                .id("1")
                .name("product")
                .images(List.of(new ImageDto("file-1"), new ImageDto("file-2")))
                .build();

        var sku1 = SkuWithProductDto.builder()
                .ean(ean1.toString())
                .price(new MoneyDto(price1.getAmount(), price1.getCurrency().getCurrencyCode()))
                .quantity(availableQuantity1)
                .attributes(List.of(new AttributeDto(1L, "size", "XL")))
                .productId(product.getId())
                .product(product)
                .build();
        var sku2 = SkuWithProductDto.builder()
                .ean(ean2.toString())
                .price(new MoneyDto(price2.getAmount(), price2.getCurrency().getCurrencyCode()))
                .quantity(availableQuantity2)
                .attributes(List.of(new AttributeDto(1L, "size", "XXL")))
                .productId(product.getId())
                .product(product)
                .build();

        var skuMap = Map.of(
                ean1, sku1,
                ean2, sku2
        );

        when(catalogService.getSku(argThat(ArgMatchers.listContainsExactlyInAnyOrder(ean1, ean2))))
                .thenReturn(Mono.just(skuMap));

        var cart = new Cart("1");
        cart.addItem(ean1, price1, availableQuantity1);
        cart.addItem(ean2, price2, availableQuantity2);

        // When
        var dto = mapper.toCartDto(cart);

        // Then
        Assertions.assertCartEquals(cart, skuMap, dto);
    }
}
