package com.example.eshop.cart.application.usecases.query;

import com.example.eshop.cart.application.usecases.query.dto.CartDto;
import com.example.eshop.cart.domain.Cart;
import com.example.eshop.cart.domain.CartRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CartQueryServiceImpl implements CartQueryService {
    private final CartRepository cartRepository;

    public CartQueryServiceImpl(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    @Override
    @PreAuthorize("#customerId == principal.getCustomerId()")
    @Transactional
    public CartDto getForCustomer(String customerId) {
        return cartRepository.findByNaturalId(customerId)
                .map(CartDto::new)
                .orElseThrow(() -> new CartNotFoundException("Cart for customer " + customerId + " not found"));
    }
}
