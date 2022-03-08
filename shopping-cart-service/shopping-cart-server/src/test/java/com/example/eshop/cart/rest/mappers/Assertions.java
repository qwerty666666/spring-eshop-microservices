package com.example.eshop.cart.rest.mappers;

import com.example.eshop.cart.domain.Cart;
import com.example.eshop.cart.domain.CartItem;
import com.example.eshop.catalog.client.SkuWithProductDto;
import com.example.eshop.cart.client.api.model.AttributeDto;
import com.example.eshop.cart.client.api.model.CartDto;
import com.example.eshop.cart.client.api.model.CartItemDto;
import com.example.eshop.cart.client.api.model.ImageDto;
import com.example.eshop.cart.client.api.model.MoneyDto;
import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import com.example.eshop.sharedkernel.domain.valueobject.Money;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import static org.assertj.core.api.Assertions.assertThat;

public class Assertions {
    public static <T1, T2> void assertListEquals(List<T1> list1, List<T2> list2, BiConsumer<T1, T2> itemAssertion) {
        assertThat(list1).as("list size").hasSize(list2.size());

        for (int i = 0; i < list1.size(); i++) {
            itemAssertion.accept(list1.get(i), list2.get(i));
        }
    }

    public static void assertPriceEquals(Money money, MoneyDto moneyDto) {
        assertThat(moneyDto.getAmount()).as("price amount").isEqualTo(money.getAmount());
        assertThat(moneyDto.getCurrency()).as("price currency").isEqualTo(money.getCurrency().getCurrencyCode());
    }

    public static void assertAttributeEquals(AttributeDto attributeDto, com.example.eshop.catalog.client.api.model.AttributeDto attributeValue) {
        assertThat(attributeDto.getId()).as("Attribute ID")
                .isEqualTo(attributeValue.getId().toString());
        assertThat(attributeDto.getName()).as("Attribute Name").isEqualTo(attributeValue.getName());
        assertThat(attributeDto.getValue()).as("Attribute Value").isEqualTo(attributeValue.getValue());
    }

    public static void assertImageEquals(List<com.example.eshop.catalog.client.api.model.ImageDto> images, List<ImageDto> imageDtos) {
        // check only collection size because we don't know what URL will be used in imageDto
        assertThat(images).hasSize(imageDtos.size());
    }

    public static void assertCartEquals(Cart cart, Map<Ean, SkuWithProductDto> productInfo, CartDto dto) {
        // id
        assertThat(dto.getId()).isEqualTo(cart.getId() == null ? null : cart.getId().toString());
        // items
        Assertions.assertListEquals(cart.getItems(), dto.getItems(), (item, itemDto) -> {
            assertCartItemEquals(item, productInfo.get(item.getEan()), itemDto);
        });
    }

    private static void assertCartItemEquals(CartItem item, SkuWithProductDto sku, CartItemDto dto) {
        var product = sku.getProduct();

        // ean
        assertThat(dto.getEan()).isEqualTo(item.getEan().toString());
        // name
        assertThat(dto.getProductName()).isEqualTo(product.getName());
        // quantity
        assertThat(dto.getQuantity()).isEqualTo(item.getQuantity());
        // available quantity
        assertThat(dto.getAvailableQuantity()).isEqualTo(sku.getQuantity());
        // price
        Assertions.assertPriceEquals(item.getItemPrice(), dto.getPrice());
        // images
        Assertions.assertImageEquals(product.getImages(), dto.getImages());
        // attributes
        Assertions.assertListEquals(dto.getAttributes(), sku.getAttributes(), Assertions::assertAttributeEquals);
    }
}
