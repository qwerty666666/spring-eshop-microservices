package com.example.eshop.checkout;

import com.example.eshop.cart.client.model.AttributeDto;
import com.example.eshop.cart.client.model.CartDto;
import com.example.eshop.cart.client.model.CartItemDto;
import com.example.eshop.cart.client.model.ImageDto;
import com.example.eshop.checkout.application.services.CreateOrderDto;
import com.example.eshop.checkout.application.services.checkoutprocess.dto.CheckoutForm;
import com.example.eshop.checkout.client.model.DeliveryAddressDto;
import com.example.eshop.checkout.domain.order.DeliveryAddress;
import com.example.eshop.checkout.domain.order.Order;
import com.example.eshop.checkout.stubs.DeliveryServiceStub;
import com.example.eshop.checkout.stubs.PaymentServiceStub;
import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import com.example.eshop.sharedkernel.domain.valueobject.Money;
import com.example.eshop.sharedkernel.domain.valueobject.Phone;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FakeData {
    public static DeliveryAddress deliveryAddress() {
        return new DeliveryAddress(fullname(), phone(), country(), city(), street(), building(), flat());
    }

    public static DeliveryAddressDto deliveryAddressDto() {
        return new DeliveryAddressDto()
                .country(country())
                .city(city())
                .street(street())
                .building(building())
                .flat(flat())
                .fullname(fullname())
                .phone(phone().toString());
    }
    
    public static String fullname() {
        return "fullname";
    }

    public static String country() {
        return "country";
    }

    public static String city() {
        return "city";
    }

    public static String building() {
        return "building";
    }

    public static String street() {
        return "street";
    }

    public static String flat() {
        return "flat";
    }
    
    public static Phone phone() {
        return Phone.fromString("+79993334444");
    }

    public static String customerId() {
        return "customerId";
    }

    public static Ean ean() {
        return Ean.fromString("1234567890123");
    }

    public static CartDto emptyCartDto() {
        return new CartDto()
                .id("1")
                .totalPrice(Money.ZERO);
    }

    public static CartDto cartDto() {
        var skuPrice = Money.USD(10);
        var quantity = 2;
        var cartPrice = Money.USD(20);

        return new CartDto()
                .id("1")
                .totalPrice(cartPrice)
                .items(List.of(
                        new CartItemDto()
                                .ean(ean())
                                .price(skuPrice)
                                .quantity(quantity)
                                .availableQuantity(quantity)
                                .productName("productName")
                                .attributes(List.of(
                                        new AttributeDto("1", "size", "42")
                                ))
                                .images(List.of(
                                        new ImageDto("url")
                                ))
                ));
    }

    public static CheckoutForm checkoutForm() {
        return CheckoutForm.builder()
                .order(order())
                .availableDeliveries(List.of(new DeliveryServiceStub(true)))
                .availablePayments(List.of(new PaymentServiceStub(true)))
                .build();
    }

    public static Order order() {
        return new Order(UUID.randomUUID(), customerId(), cartDto(), deliveryAddress(), null, null);
    }

    public static CreateOrderDto createOrderDto() {
        return createOrderDto(customerId());
    }

    public static CreateOrderDto createOrderDto(String customerId) {
        return CreateOrderDto.builder()
                .customerId("nonAuthorizedCustomerId")
                .cart(FakeData.cartDto())
                .address(FakeData.deliveryAddress())
                .build();
    }
}
