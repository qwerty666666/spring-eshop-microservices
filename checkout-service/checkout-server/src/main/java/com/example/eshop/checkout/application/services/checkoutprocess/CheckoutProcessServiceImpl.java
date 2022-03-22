package com.example.eshop.checkout.application.services.checkoutprocess;

import com.example.eshop.checkout.application.services.CreateOrderDto;
import com.example.eshop.checkout.application.services.OrderFactory;
import com.example.eshop.checkout.application.services.checkoutprocess.dto.CheckoutForm;
import com.example.eshop.checkout.domain.delivery.DeliveryService;
import com.example.eshop.checkout.domain.delivery.DeliveryServiceRepository;
import com.example.eshop.checkout.domain.order.Order;
import com.example.eshop.checkout.domain.payment.PaymentService;
import com.example.eshop.checkout.domain.payment.PaymentServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CheckoutProcessServiceImpl implements CheckoutProcessService {
    private final DeliveryServiceRepository deliveryServiceRepository;
    private final PaymentServiceRepository paymentServiceRepository;
    private final OrderFactory orderFactory;

    @Override
    @PreAuthorize("#createOrderDto.customerId() == authentication.getCustomerId()")
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
