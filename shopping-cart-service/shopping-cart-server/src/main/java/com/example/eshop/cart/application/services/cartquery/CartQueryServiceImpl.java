package com.example.eshop.cart.application.services.cartquery;

import com.example.eshop.cart.application.services.createcart.CreateCartService;
import com.example.eshop.cart.domain.Cart;
import com.example.eshop.cart.domain.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CartQueryServiceImpl implements CartQueryService {
    private final CartRepository cartRepository;
    private final CreateCartService createCartService;

    @Override
    @PreAuthorize("#customerId == authentication.getCustomerId()")
    @Transactional
    public Cart getForCustomerOrCreate(String customerId) {
        return cartRepository.findByNaturalId(customerId)
                .orElseGet(() -> createCartService.create(customerId));
    }
}
