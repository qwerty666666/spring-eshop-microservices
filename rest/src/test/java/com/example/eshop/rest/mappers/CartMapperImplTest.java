package com.example.eshop.rest.mappers;

import com.example.eshop.cart.domain.cart.Cart;
import com.example.eshop.catalog.client.api.model.Attribute;
import com.example.eshop.catalog.client.api.model.Image;
import com.example.eshop.catalog.client.api.model.Product;
import com.example.eshop.catalog.client.api.model.Sku;
import com.example.eshop.catalog.client.cataloggateway.CatalogGateway;
import com.example.eshop.rest.config.MappersTest;
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

@MappersTest
class CartMapperImplTest {
    @MockBean
    private CatalogGateway catalogGateway;

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

        var product = Product.builder()
                .name("product")
                .images(List.of(new Image("file-1"), new Image("file-2")))
                .sku(List.of(
                        Sku.builder()
                                .ean(ean1.toString())
                                .price(new com.example.eshop.catalog.client.api.model.Money(price1.getAmount(), price1.getCurrency().getCurrencyCode()))
                                .quantity(availableQuantity1)
                                .attributes(List.of(new Attribute("1", "size", "XL")))
                                .build(),
                        Sku.builder()
                                .ean(ean2.toString())
                                .price(new com.example.eshop.catalog.client.api.model.Money(price2.getAmount(), price2.getCurrency().getCurrencyCode()))
                                .quantity(availableQuantity2)
                                .attributes(List.of(new Attribute("1", "size", "XXL")))
                                .build()
                ))
                .build();

        var products = Map.of(
                ean1, product,
                ean2, product
        );

        when(catalogGateway.getProductsByEan(argThat(ArgMatchers.listContainsExactlyInAnyOrder(ean1, ean2))))
                .thenReturn(Mono.just(products));

        var cart = new Cart("1");
        cart.addItem(ean1, price1, availableQuantity1);
        cart.addItem(ean2, price2, availableQuantity2);

        // When
        var dto = mapper.toCartDto(cart);

        // Then
        Assertions.assertCartEquals(cart, products, dto);
    }
}
