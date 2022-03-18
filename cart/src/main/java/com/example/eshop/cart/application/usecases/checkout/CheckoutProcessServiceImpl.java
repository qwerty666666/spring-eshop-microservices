package com.example.eshop.cart.application.usecases.checkout;

import com.example.eshop.cart.domain.checkout.order.CreateOrderDto;
import com.example.eshop.cart.domain.checkout.order.Order;
import com.example.eshop.cart.domain.checkout.order.OrderFactory;
import com.example.eshop.cart.domain.checkout.delivery.DeliveryServiceRepository;
import com.example.eshop.cart.domain.checkout.delivery.DeliveryService;
import com.example.eshop.cart.domain.checkout.payment.PaymentService;
import com.example.eshop.cart.domain.checkout.payment.PaymentServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CheckoutProcessServiceImpl implements CheckoutProcessService {
    private final DeliveryServiceRepository deliveryServiceRepository;
    private final PaymentServiceRepository paymentServiceRepository;
    private final OrderFactory orderFactory;

    @Override
    @PreAuthorize("#createOrderDto.customerId() == authentication.getCustomerId()")
    @Transactional(readOnly = true)
    public CheckoutForm process(CreateOrderDto createOrderDto) {
        var order = orderFactory.create(createOrderDto);

        var availableDeliveries = getAvailableDeliveries(order);
        var availablePayments = getAvailablePayments(order);

        return CheckoutForm.builder()
                .order(order)
                .availableDeliveries(availableDeliveries)
                .availablePayments(availablePayments)
                .build();
    }

    private List<DeliveryService> getAvailableDeliveries(Order order) {
        return deliveryServiceRepository.findAll().stream()
                .filter(deliveryService -> deliveryService.canDeliver(order))
                .toList();
    }

    private List<PaymentService> getAvailablePayments(Order order) {
        return paymentServiceRepository.findAll().stream()
                .filter(paymentService -> paymentService.canPay(order))
                .toList();
    }
}
