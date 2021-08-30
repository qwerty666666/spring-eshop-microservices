package com.example.eshop.cart.application.usecases.create;

import com.example.eshop.cart.domain.Cart;
import com.example.eshop.cart.domain.CartRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
public class CreateCartServiceImpl implements CreateCartService {
    private final CartRepository cartRepository;

    public CreateCartServiceImpl(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    @Override
    @Transactional
    public void create(String customerId) {
        var cart = new Cart(customerId);

        cartRepository.save(cart);

        log.info("Cart created for customer " + customerId);
    }
}
