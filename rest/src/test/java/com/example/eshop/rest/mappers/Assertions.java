package com.example.eshop.rest.mappers;

import com.example.eshop.cart.domain.cart.Cart;
import com.example.eshop.cart.domain.cart.CartItem;
import com.example.eshop.catalog.client.model.SkuWithProductDto;
import com.example.eshop.rest.dto.AttributeDto;
import com.example.eshop.rest.dto.CartDto;
import com.example.eshop.rest.dto.CartItemDto;
import com.example.eshop.rest.dto.ImageDto;
import com.example.eshop.sharedkernel.domain.valueobject.Ean;
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

    public static void assertAttributeEquals(AttributeDto attributeDto, com.example.eshop.catalog.client.model.AttributeDto attributeValue) {
        assertThat(attributeDto.getId()).as("Attribute ID")
                .isEqualTo(attributeValue.getId() == null ? null : attributeValue.getId().toString());
        assertThat(attributeDto.getName()).as("Attribute Name").isEqualTo(attributeValue.getName());
        assertThat(attributeDto.getValue()).as("Attribute Value").isEqualTo(attributeValue.getValue());
    }

    public static void assertImageEquals(List<com.example.eshop.catalog.client.model.ImageDto> images, List<ImageDto> imageDtos) {
        // check only collection size because we don't know what URL will be used in imageDto
        assertThat(images).hasSize(imageDtos.size());
    }
}
