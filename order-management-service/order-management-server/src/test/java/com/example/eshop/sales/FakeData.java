package com.example.eshop.sales;

import com.example.eshop.catalog.client.SkuWithProductDto;
import com.example.eshop.catalog.client.api.model.AttributeDto;
import com.example.eshop.catalog.client.api.model.ImageDto;
import com.example.eshop.catalog.client.api.model.MoneyDto;
import com.example.eshop.catalog.client.api.model.ProductDto;
import com.example.eshop.checkout.client.events.orderplacedevent.CartDto;
import com.example.eshop.checkout.client.events.orderplacedevent.CartItemDto;
import com.example.eshop.checkout.client.events.orderplacedevent.DeliveryAddressDto;
import com.example.eshop.checkout.client.events.orderplacedevent.DeliveryDto;
import com.example.eshop.checkout.client.events.orderplacedevent.DeliveryServiceDto;
import com.example.eshop.checkout.client.events.orderplacedevent.OrderDto;
import com.example.eshop.checkout.client.events.orderplacedevent.PaymentDto;
import com.example.eshop.checkout.client.events.orderplacedevent.PaymentServiceDto;
import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import com.example.eshop.sharedkernel.domain.valueobject.Money;
import com.example.eshop.sharedkernel.domain.valueobject.Phone;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.UUID;
import java.util.random.RandomGenerator;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FakeData {
    private static final RandomGenerator random = RandomGenerator.getDefault();

    public static OrderDto orderDto() {
        var ean = Ean.fromString("9578495810658");
        var skuPrice = Money.USD(10);
        var quantity = 2;
        var cartPrice = Money.USD(20);
        var deliveryPrice = Money.USD(12);
        var totalPrice = Money.USD(32);
        var product = ProductDto.builder()
                .id("123")
                .name("fake product")
                .description("descr")
                .images(List.of(new ImageDto("url")))
                .build();
        var sku = SkuWithProductDto.builder()
                .ean(ean.toString())
                .quantity(quantity)
                .price(new MoneyDto(skuPrice.getAmount(), skuPrice.getCurrency().toString()))
                .productId(product.getId())
                .product(product)
                .attributes(List.of(
                        new AttributeDto(1L, "attr_name", "attr_value")
                ))
                .build();

        return OrderDto.builder()
                .id(UUID.randomUUID())
                .customerId("customerId")
                .cart(new CartDto(
                        cartPrice,
                        List.of(new CartItemDto(ean, skuPrice.multiply(quantity), quantity, sku))
                ))
                .totalPrice(totalPrice)
                .delivery(new DeliveryDto(
                        new DeliveryAddressDto("fullname", Phone.fromString("+79999999999"), "country", "city", "street", "building", "flat"),
                        new DeliveryServiceDto("deliveryServiceId", "deliveryServiceName"),
                        deliveryPrice
                ))
                .payment(new PaymentDto(
                        new PaymentServiceDto("paymentServiceId", "paymentServiceName")
                ))
                .build();
    }
}
