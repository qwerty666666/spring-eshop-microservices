package com.example.eshop.cart.application.eventlisteners;

import com.example.eshop.cart.application.usecases.createcart.CreateCartService;
import com.example.eshop.customer.domain.customer.CustomerCreatedEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@SpringBootTest
class CustomerCreatedEventListenerIntegrationTest {
    @MockBean
    CreateCartService createCartService;

    @Autowired
    ApplicationEventPublisher eventPublisher;

    @Test
    void whenCustomerCreatedEventFired_thenCartIsCreated() {
        var customerId = "1";
        var event = new CustomerCreatedEvent(customerId);

        eventPublisher.publishEvent(event);

        verify(createCartService).create(eq(customerId));
    }
}
