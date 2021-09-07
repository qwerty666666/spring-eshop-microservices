package com.example.eshop.cart.application.eventlisteners;

import com.example.eshop.cart.application.usecases.createcart.CartAlreadyExistException;
import com.example.eshop.cart.application.usecases.createcart.CreateCartService;
import com.example.eshop.customer.domain.customer.CustomerCreatedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class CustomerCreatedEventListener {
    private final CreateCartService createCartService;

    public CustomerCreatedEventListener(CreateCartService createCartService) {
        this.createCartService = createCartService;
    }

    @EventListener
    public void createCart(CustomerCreatedEvent event) {
        try {
            createCartService.create(event.customerId());
        } catch (CartAlreadyExistException e) {
            // if the cart was already created before for some reason,
            // then we just ignore it and proceed event successfully
        }
    }
}
