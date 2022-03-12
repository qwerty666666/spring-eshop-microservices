package com.example.eshop.cart.application.services.createcart;

import com.example.eshop.cart.domain.Cart;
import com.example.eshop.cart.domain.CartRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class CreateCartServiceImpl implements CreateCartService {
    private final CartRepository cartRepository;

    @Override
    @Transactional
    public Cart create(String customerId) {
        var cart = new Cart(customerId);

        cartRepository.save(cart);

        log.info("Cart created for customer " + customerId);

        return cart;
    }
}
