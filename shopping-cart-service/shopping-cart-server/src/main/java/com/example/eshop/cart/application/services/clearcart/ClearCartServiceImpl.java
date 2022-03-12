package com.example.eshop.cart.application.services.clearcart;

import com.example.eshop.cart.application.services.cartquery.CartQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClearCartServiceImpl implements ClearCartService {
    private final CartQueryService cartQueryService;

    @Override
    @PreAuthorize("#customerId == authentication.getCustomerId()")
    @Transactional
    public void clear(String customerId) {
        var cart = cartQueryService.getForCustomerOrCreate(customerId);

        cart.clear();
    }
}
