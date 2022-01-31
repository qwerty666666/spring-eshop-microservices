package com.example.eshop.rest.mappers;

import com.example.eshop.cart.application.usecases.checkout.CheckoutForm;
import com.example.eshop.cart.domain.cart.Cart;
import com.example.eshop.cart.domain.checkout.delivery.DeliveryService.DeliveryServiceId;
import com.example.eshop.cart.domain.checkout.order.CreateOrderDto;
import com.example.eshop.cart.domain.checkout.order.DeliveryAddress;
import com.example.eshop.cart.domain.checkout.payment.PaymentService.PaymentServiceId;
import com.example.eshop.rest.dto.CheckoutFormDto;
import com.example.eshop.rest.dto.CheckoutRequestDto;
import com.example.eshop.rest.dto.DeliveryAddressDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.lang.Nullable;

@Mapper(
        componentModel = "spring",
        uses = { CartMapper.class, PhoneMapper.class }
)
public interface CheckoutMapper {
    CreateOrderDto toCreateOrderDto(CheckoutRequestDto checkoutRequest, String customerId, Cart cart);

    DeliveryAddress toDeliveryAddress(DeliveryAddressDto dto);

    DeliveryAddressDto toDeliveryAddressDto(DeliveryAddress address);

    @Nullable
    default DeliveryServiceId toDeliveryServiceId(@Nullable String id) {
        return id == null ? null : new DeliveryServiceId(id);
    }

    @Nullable
    default String toString(@Nullable DeliveryServiceId id) {
        return id == null ? null : id.toString();
    }

    @Nullable
    default PaymentServiceId toPaymentServiceId(@Nullable String id) {
        return id == null ? null : new PaymentServiceId(id);
    }

    @Nullable
    default String toString(@Nullable PaymentServiceId id) {
        return id == null ? null : id.toString();
    }

    @Mapping(target = "deliveryAddress", source = "order.address")
    @Mapping(target = "cart", source = "order.cart")
    CheckoutFormDto toCheckoutFormDto(CheckoutForm form);
}
