package com.example.eshop.cart.domain.checkout.order;

import com.example.eshop.cart.domain.cart.Cart;
import com.example.eshop.cart.domain.checkout.delivery.DeliveryService;
import com.example.eshop.cart.domain.checkout.delivery.DeliveryService.DeliveryServiceId;
import com.example.eshop.cart.domain.checkout.delivery.DeliveryServiceRepository;
import com.example.eshop.cart.domain.checkout.payment.PaymentServiceNotFoundException;
import com.example.eshop.cart.domain.checkout.delivery.DeliveryServiceNotFoundException;
import com.example.eshop.cart.domain.checkout.payment.PaymentService;
import com.example.eshop.cart.domain.checkout.payment.PaymentService.PaymentServiceId;
import com.example.eshop.cart.domain.checkout.payment.PaymentServiceRepository;
import com.example.eshop.sharedkernel.domain.validation.Errors;
import com.example.eshop.sharedkernel.domain.validation.ValidationException;
import com.example.eshop.sharedkernel.domain.Localizer;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderFactoryImpl implements OrderFactory {
    private final DeliveryServiceRepository deliveryServiceRepository;
    private final PaymentServiceRepository paymentServiceRepository;
    private final Localizer localizer;

    @Override
    public Order create(CreateOrderDto createOrderDto) {
        var errors = validate(createOrderDto);

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }

        var delivery = createOrderDto.deliveryServiceId() == null ? null :
                getDeliveryService(createOrderDto.deliveryServiceId());
        var payment = createOrderDto.paymentServiceId() == null ? null :
                getPaymentService(createOrderDto.paymentServiceId());

        return new Order(
                createOrderDto.customerId(),
                createOrderDto.cart().clone(),      // clone cart to avoid modifications
                createOrderDto.address(),
                delivery,
                payment
        );
    }

    private Errors validate(CreateOrderDto dto) {
        var errors = new Errors();

        validateCustomer(dto.customerId(), errors);
        validateAddress(dto.address(), errors);
        validateCart(dto.cart(), errors);
        validateDelivery(dto.deliveryServiceId(), errors);
        validatePayment(dto.paymentServiceId(), errors);

        return errors;
    }

    private void validateCustomer(@Nullable String customerId, Errors errors) {
        if (customerId == null) {
            errors.addError(CreateOrderDto.CUSTOMER_ID_FIELD, localizer.getMessage("customerId.null"));
        }
    }

    private void validateAddress(@Nullable DeliveryAddress address, Errors errors) {
        if (address == null) {
            errors.addError(CreateOrderDto.ADDRESS_FIELD, localizer.getMessage("address.null"));
        }
    }

    private void validateCart(@Nullable Cart cart, Errors errors) {
        if (cart == null) {
            errors.addError(CreateOrderDto.CART_FIELD, localizer.getMessage("cart.null"));
            return;
        }

        if (cart.isEmpty()) {
            errors.addError(CreateOrderDto.CART_FIELD, localizer.getMessage("cart.empty"));
        }
    }

    private void validateDelivery(@Nullable DeliveryServiceId deliveryId, Errors errors) {
        if (deliveryId != null && deliveryServiceRepository.findById(deliveryId).isEmpty()) {
            errors.addError(CreateOrderDto.DELIVERY_SERVICE_ID_FIELD, localizer.getMessage("delivery.notExist"));
        }
    }

    private void validatePayment(@Nullable PaymentServiceId paymentId, Errors errors) {
        if (paymentId != null && paymentServiceRepository.findById(paymentId).isEmpty()) {
            errors.addError(CreateOrderDto.PAYMENT_SERVICE_ID_FIELD, localizer.getMessage("payment.notExist"));
        }
    }

    private DeliveryService getDeliveryService(DeliveryServiceId deliveryServiceId) {
        return deliveryServiceRepository.findById(deliveryServiceId)
                .orElseThrow(() -> new DeliveryServiceNotFoundException("Delivery " + deliveryServiceId + " not found"));
    }

    private PaymentService getPaymentService(PaymentServiceId paymentId) {
        return paymentServiceRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentServiceNotFoundException("Payment " + paymentId + " not found"));
    }
}
