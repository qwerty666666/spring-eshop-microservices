package com.example.eshop.checkout.application.services;

import com.example.eshop.cart.client.model.CartDto;
import com.example.eshop.checkout.domain.delivery.DeliveryService;
import com.example.eshop.checkout.domain.delivery.DeliveryService.DeliveryServiceId;
import com.example.eshop.checkout.domain.delivery.DeliveryServiceRepository;
import com.example.eshop.checkout.domain.order.DeliveryAddress;
import com.example.eshop.checkout.domain.order.Order;
import com.example.eshop.checkout.domain.payment.PaymentService;
import com.example.eshop.checkout.domain.payment.PaymentService.PaymentServiceId;
import com.example.eshop.checkout.domain.payment.PaymentServiceRepository;
import com.example.eshop.sharedkernel.domain.validation.Errors;
import com.example.eshop.sharedkernel.domain.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderFactoryImpl implements OrderFactory {
    private final DeliveryServiceRepository deliveryServiceRepository;
    private final PaymentServiceRepository paymentServiceRepository;

    @Override
    public Order create(CreateOrderDto createOrderDto) {
        validate(createOrderDto);

        var delivery = getDeliveryService(createOrderDto.deliveryServiceId());
        var payment = getPaymentService(createOrderDto.paymentServiceId());

        return new Order(
                UUID.randomUUID(),
                createOrderDto.customerId(),
                createOrderDto.cart(),
                createOrderDto.address(),
                delivery,
                payment
        );
    }

    private void validate(CreateOrderDto dto) {
        var errors = new Errors();

        validateCustomer(dto.customerId(), errors);
        validateAddress(dto.address(), errors);
        validateCart(dto.cart(), errors);
        validateDelivery(dto.deliveryServiceId(), errors);
        validatePayment(dto.paymentServiceId(), errors);

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }

    private void validateCustomer(@Nullable String customerId, Errors errors) {
        if (customerId == null) {
            errors.addError(CreateOrderDto.CUSTOMER_ID_FIELD, "customerId.null");
        }
    }

    private void validateAddress(@Nullable DeliveryAddress address, Errors errors) {
        if (address == null) {
            errors.addError(CreateOrderDto.ADDRESS_FIELD, "address.null");
        }
    }

    private void validateCart(@Nullable CartDto cart, Errors errors) {
        if (cart == null) {
            errors.addError(CreateOrderDto.CART_FIELD, "cart.null");
            return;
        }

        if (cart.getItems().isEmpty()) {
            errors.addError(CreateOrderDto.CART_FIELD, "cart.empty");
        }
    }

    private void validateDelivery(@Nullable DeliveryServiceId deliveryServiceId, Errors errors) {
        try {
            getDeliveryService(deliveryServiceId);
        } catch (DeliveryServiceNotFoundException e) {
            errors.addError(CreateOrderDto.DELIVERY_SERVICE_ID_FIELD, "delivery.notExist");
        }
    }

    private void validatePayment(@Nullable PaymentServiceId paymentServiceId, Errors errors) {
        try {
            getPaymentService(paymentServiceId);
        } catch (PaymentServiceNotFoundException e) {
            errors.addError(CreateOrderDto.PAYMENT_SERVICE_ID_FIELD, "payment.notExist");
        }
    }

    /**
     * @throws DeliveryServiceNotFoundException if payment with given id does not exist
     */
    @Nullable
    private DeliveryService getDeliveryService(@Nullable DeliveryServiceId deliveryServiceId) {
        if (deliveryServiceId == null) {
            return null;
        }

        return deliveryServiceRepository.findById(deliveryServiceId)
                .orElseThrow(() -> new DeliveryServiceNotFoundException("Delivery " + deliveryServiceId + " not found"));
    }

    /**
     * @throws PaymentServiceNotFoundException if payment with given id does not exist
     */
    @Nullable
    private PaymentService getPaymentService(@Nullable PaymentServiceId paymentServiceId) {
        if (paymentServiceId == null) {
            return null;
        }

        return paymentServiceRepository.findById(paymentServiceId)
                .orElseThrow(() -> new PaymentServiceNotFoundException("Payment " + paymentServiceId + " not found"));
    }
}
