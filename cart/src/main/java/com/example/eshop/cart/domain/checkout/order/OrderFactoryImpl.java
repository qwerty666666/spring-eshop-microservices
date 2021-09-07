package com.example.eshop.cart.domain.checkout.order;

import com.example.eshop.cart.domain.checkout.placeorder.PlaceOrderDto;
import com.example.eshop.cart.domain.checkout.delivery.DeliveryService;
import com.example.eshop.cart.domain.checkout.delivery.DeliveryService.DeliveryServiceId;
import com.example.eshop.cart.domain.checkout.delivery.DeliveryServiceRepository;
import com.example.eshop.cart.domain.checkout.payment.PaymentServiceNotAvailableException;
import com.example.eshop.cart.domain.checkout.payment.PaymentServiceNotFoundException;
import com.example.eshop.cart.domain.checkout.delivery.ShipmentNotAvailableException;
import com.example.eshop.cart.domain.checkout.delivery.DeliveryServiceNotFoundException;
import com.example.eshop.cart.domain.checkout.payment.PaymentService;
import com.example.eshop.cart.domain.checkout.payment.PaymentService.PaymentServiceId;
import com.example.eshop.cart.domain.checkout.payment.PaymentServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderFactoryImpl implements OrderFactory {
    private final DeliveryServiceRepository deliveryServiceRepository;
    private final PaymentServiceRepository paymentServiceRepository;

    @Override
    public Order create(PlaceOrderDto createOrderDto) {
        var order = new Order(createOrderDto.customerId(), createOrderDto.cart().clone(), createOrderDto.address());

        if (createOrderDto.deliveryServiceId() != null) {
            applyDelivery(order, createOrderDto.deliveryServiceId());
        }

        if (createOrderDto.paymentServiceId() != null) {
            applyPayment(order, createOrderDto.paymentServiceId());
        }

        return order;
    }

    /**
     * Set delivery info to {@code order}
     *
     * @throws DeliveryServiceNotFoundException if {@link DeliveryService} with given ID does not exist
     * @throws ShipmentNotAvailableException if given delivery service can't be applied to this order
     */
    private void applyDelivery(Order order, DeliveryServiceId deliveryId) {
        var deliveryService = getDeliveryService(deliveryId);

        if (!deliveryService.canDeliver(order)) {
            throw new ShipmentNotAvailableException("Delivery " + deliveryId + " not available " +
                    "for given order");
        }

        order.setDeliveryService(deliveryService);
        order.setDeliveryPrice(deliveryService.getShipmentInfo(order).price());
    }

    private DeliveryService getDeliveryService(DeliveryServiceId deliveryServiceId) {
        return deliveryServiceRepository.findById(deliveryServiceId)
                .orElseThrow(() -> new DeliveryServiceNotFoundException("Delivery " + deliveryServiceId + " not found"));
    }

    /**
     * Set payment info to {@code order}
     *
     * @throws PaymentServiceNotFoundException if {@link PaymentService} with given ID does not exist
     * @throws PaymentServiceNotAvailableException if given payment service can't be applied to this order
     */
    private void applyPayment(Order order, PaymentServiceId paymentServiceId) {
        var paymentService = getPaymentService(paymentServiceId);

        if (!paymentService.canPay(order)) {
            throw new PaymentServiceNotAvailableException("Payment " + paymentServiceId + " is not available for this order");
        }

        order.setPaymentService(paymentService);
    }

    private PaymentService getPaymentService(PaymentServiceId paymentId) {
        return paymentServiceRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentServiceNotFoundException("Payment " + paymentId + " not found"));
    }
}
