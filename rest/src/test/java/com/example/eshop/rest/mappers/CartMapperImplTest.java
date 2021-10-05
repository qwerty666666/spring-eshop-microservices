package com.example.eshop.rest.mappers;

import com.example.eshop.cart.application.usecases.cartquery.dto.CartDto;
import com.example.eshop.cart.application.usecases.cartquery.dto.CartItemDto;
import com.example.eshop.cart.domain.cart.Cart;
import com.example.eshop.rest.config.MappersConfig;
import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import com.example.eshop.sharedkernel.domain.valueobject.Money;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = MappersConfig.class)
class CartMapperImplTest {
    @Autowired
    CartMapper mapper;

    @Test
    void testToCartDto() {
        // Given
        var cart = new CartDto(createCart());

        // When
        var dto = mapper.toCartDto(cart);
        
        // Then
        assertCartEquals(cart, dto);
    }

    private Cart createCart() {
        var cart = new Cart("1");
        cart.addItem(Ean.fromString("1111111111111"), Money.USD(10), 10, "product1");
        cart.addItem(Ean.fromString("2222222222222"), Money.USD(20), 20, "product2");

        return cart;
    }

    private static void assertCartEquals(CartDto cart1, com.example.eshop.rest.dto.CartDto cart2) {
        assertThat(cart2.getId()).as("ID").isEqualTo(cart1.id());
        Utils.assertListEquals(cart1.items(), cart2.getItems(), CartMapperImplTest::assertCartItemEquals);
    }

    private static void assertCartItemEquals(CartItemDto item1, com.example.eshop.rest.dto.CartItemDto item2) {
        assertThat(item2.getEan()).as("EAN").isEqualTo(item1.ean().toString());
        assertThat(item2.getProductName()).as("Product Name").isEqualTo(item1.productName());
        assertThat(item2.getQuantity()).as("Quantity").isEqualTo(item1.quantity());
        Utils.assertPriceEquals(item1.price(), item2.getPrice());
    }
}
