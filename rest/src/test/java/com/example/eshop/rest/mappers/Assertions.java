package com.example.eshop.rest.mappers;

import com.example.eshop.cart.domain.cart.Cart;
import com.example.eshop.cart.domain.cart.CartItem;
import com.example.eshop.catalog.client.api.model.Attribute;
import com.example.eshop.catalog.client.api.model.Image;
import com.example.eshop.catalog.client.api.model.Product;
import com.example.eshop.catalog.client.cataloggateway.SkuWithProduct;
import com.example.eshop.rest.dto.AttributeDto;
import com.example.eshop.rest.dto.CartDto;
import com.example.eshop.rest.dto.CartItemDto;
import com.example.eshop.rest.dto.ImageDto;
import com.example.eshop.rest.dto.MoneyDto;
import com.example.eshop.rest.dto.PageableDto;
import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import com.example.eshop.sharedkernel.domain.valueobject.Money;
import org.springframework.data.domain.Page;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import static org.assertj.core.api.Assertions.assertThat;

public class Assertions {
    public static void assertPageableEquals(Page<?> page, PageableDto pageableDto) {
        assertThat(pageableDto.getPage()).as("page number").isEqualTo(page.getNumber() + 1);
        assertThat(pageableDto.getPerPage()).as("page size").isEqualTo(page.getSize());
        assertThat(pageableDto.getTotalPages()).as("total pages").isEqualTo(page.getTotalPages());
        assertThat(pageableDto.getTotalItems()).as("total items").isEqualTo((int)page.getTotalElements());
    }

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

    public static void assertAttributeEquals(AttributeDto attributeDto, Attribute attributeValue) {
        assertThat(attributeDto.getId()).as("Attribute ID")
                .isEqualTo(attributeValue.getId() == null ? null : attributeValue.getId());
        assertThat(attributeDto.getName()).as("Attribute Name").isEqualTo(attributeValue.getName());
        assertThat(attributeDto.getValue()).as("Attribute Value").isEqualTo(attributeValue.getValue());
    }

    public static void assertImageEquals(List<Image> images, List<ImageDto> imageDtos) {
        // check only collection size because we don't know what URL will be used in imageDto
        assertThat(images).hasSize(imageDtos.size());
    }

    public static void assertCartEquals(Cart cart, Map<Ean, SkuWithProduct> productInfo, CartDto dto) {
        // id
        assertThat(dto.getId()).isEqualTo(cart.getId() == null ? null : cart.getId().toString());
        // items
        Assertions.assertListEquals(cart.getItems(), dto.getItems(), (item, itemDto) -> {
            assertCartItemEquals(item, productInfo.get(item.getEan()), itemDto);
        });
    }

    private static void assertCartItemEquals(CartItem item, SkuWithProduct sku, CartItemDto dto) {
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
