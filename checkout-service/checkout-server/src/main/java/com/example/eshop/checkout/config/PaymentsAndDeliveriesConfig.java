package com.example.eshop.checkout.config;

import com.example.eshop.checkout.domain.delivery.DeliveryService.DeliveryServiceId;
import com.example.eshop.checkout.domain.delivery.DeliveryServiceRepository;
import com.example.eshop.checkout.domain.delivery.ExpressDeliveryService;
import com.example.eshop.checkout.domain.delivery.StandardDeliveryService;
import com.example.eshop.checkout.domain.payment.CashPaymentService;
import com.example.eshop.checkout.domain.payment.PaymentService.PaymentServiceId;
import com.example.eshop.checkout.domain.payment.PaymentServiceRepository;
import com.example.eshop.checkout.infrastructure.InMemoryDeliveryServiceRepository;
import com.example.eshop.checkout.infrastructure.InMemoryPaymentServiceRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;

@Configuration
public class PaymentsAndDeliveriesConfig {
    @Bean
    public DeliveryServiceRepository inMemoryDeliveryServiceRepository() {
        return new InMemoryDeliveryServiceRepository(List.of(
                new StandardDeliveryService(new DeliveryServiceId("standard"), "Standard"),
                new ExpressDeliveryService(new DeliveryServiceId("express"), "Express")
        ));
    }

    @Bean
    public PaymentServiceRepository inMemoryPaymentServiceRepository() {
        return new InMemoryPaymentServiceRepository(List.of(
                new CashPaymentService(new PaymentServiceId("cash"), "Cash")
        ));
    }
}
