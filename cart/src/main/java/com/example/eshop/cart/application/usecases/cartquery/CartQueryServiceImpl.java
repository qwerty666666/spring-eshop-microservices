package com.example.eshop.cart.application.usecases.cartquery;

import com.example.eshop.cart.domain.cart.Cart;
import com.example.eshop.cart.domain.cart.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CartQueryServiceImpl implements CartQueryService {
    private final CartRepository cartRepository;

    @Override
    @PreAuthorize("#customerId == authentication.getCustomerId()")
    @Transactional
    public Cart getForCustomer(String customerId) {
        return cartRepository.findByNaturalId(customerId)
                .orElseThrow(() -> new CartNotFoundException("Cart for customer " + customerId + " not found"));
    }
}
