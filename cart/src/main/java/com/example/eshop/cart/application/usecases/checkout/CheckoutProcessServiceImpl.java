package com.example.eshop.cart.application.usecases.checkout;

import com.example.eshop.cart.application.usecases.cartquery.CartNotFoundException;
import com.example.eshop.cart.domain.cart.CartRepository;
import com.example.eshop.cart.domain.checkout.placeorder.PlaceOrderDto;
import com.example.eshop.cart.domain.checkout.order.Order;
import com.example.eshop.cart.domain.checkout.order.OrderFactory;
import com.example.eshop.cart.domain.checkout.delivery.DeliveryServiceRepository;
import com.example.eshop.cart.domain.checkout.delivery.DeliveryService;
import com.example.eshop.cart.domain.checkout.payment.PaymentService;
import com.example.eshop.cart.domain.checkout.payment.PaymentServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CheckoutProcessServiceImpl implements CheckoutProcessService {
    private final CartRepository cartRepository;
    private final DeliveryServiceRepository deliveryServiceRepository;
    private final PaymentServiceRepository paymentServiceRepository;
    private final OrderFactory orderFactory;

    @Override
    @PreAuthorize("#orderDto.customerId() == principal.getCustomerId()")
    public CheckoutForm process(OrderDto orderDto) {
        var order = createOrder(orderDto);

        var availableDeliveries = getAvailableDeliveries(order);
        var availablePayments = getAvailablePayments(order);

        var total = getTotal(order);

        return CheckoutForm.builder()
                .order(order)
                .availableDeliveries(availableDeliveries)
                .availablePayments(availablePayments)
                .total(total)
                .build();
    }

    private Order createOrder(OrderDto orderDto) {
        var cart = cartRepository.findByNaturalId(orderDto.customerId())
                .orElseThrow(() -> new CartNotFoundException("Customer " + orderDto.customerId() + " has no Cart"));

        var createOrderDto = new PlaceOrderDto(orderDto.customerId(), cart, orderDto.address(),
                orderDto.deliveryServiceId(), orderDto.paymentServiceId());

        return orderFactory.create(createOrderDto);
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

    private Total getTotal(Order order) {
        var delivery = order.getDeliveryService();
        var shipmentInfo = delivery != null ? delivery.getShipmentInfo(order) : null;

        return new Total(order.getCart(), shipmentInfo);
    }
}
