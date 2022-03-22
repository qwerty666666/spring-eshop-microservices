package com.example.eshop.rest.mappers;

import com.example.eshop.checkout.application.services.checkoutprocess.dto.CheckoutForm;
import com.example.eshop.cart.client.model.CartDto;
import com.example.eshop.checkout.domain.delivery.DeliveryService.DeliveryServiceId;
import com.example.eshop.checkout.application.services.CreateOrderDto;
import com.example.eshop.checkout.domain.order.DeliveryAddress;
import com.example.eshop.checkout.domain.payment.PaymentService.PaymentServiceId;
import com.example.eshop.rest.dto.CheckoutFormDto;
import com.example.eshop.rest.dto.CheckoutRequestDto;
import com.example.eshop.rest.dto.DeliveryAddressDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.lang.Nullable;

@Mapper(
        componentModel = "spring",
        uses = { PhoneMapper.class }
)
public interface CheckoutMapper {
    CreateOrderDto toCreateOrderDto(CheckoutRequestDto checkoutRequest, String customerId, CartDto cart);

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
