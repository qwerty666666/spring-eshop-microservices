package com.example.eshop.cart.application.usecases.clearcart;

import com.example.eshop.cart.application.usecases.cartquery.CartQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClearCartServiceImpl implements ClearCartService {
    private final CartQueryService cartQueryService;

    @Override
    @PreAuthorize("#customerId == principal.getCustomerId()")
    @Transactional
    public void clear(String customerId) {
        var cart = cartQueryService.getForCustomer(customerId);

        cart.clear();
    }
}
