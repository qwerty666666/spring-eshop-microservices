package com.example.eshop.cart.config;

import com.example.eshop.cart.domain.checkout.delivery.DeliveryService.DeliveryServiceId;
import com.example.eshop.cart.domain.checkout.delivery.DeliveryServiceRepository;
import com.example.eshop.cart.domain.checkout.delivery.ExpressDeliveryService;
import com.example.eshop.cart.domain.checkout.delivery.StandardDeliveryService;
import com.example.eshop.cart.domain.checkout.payment.CashPaymentService;
import com.example.eshop.cart.domain.checkout.payment.PaymentService.PaymentServiceId;
import com.example.eshop.cart.domain.checkout.payment.PaymentServiceRepository;
import com.example.eshop.cart.infrastructure.InMemoryDeliveryServiceRepository;
import com.example.eshop.cart.infrastructure.InMemoryPaymentServiceRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;

@Configuration
public class CheckoutConfig {
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
