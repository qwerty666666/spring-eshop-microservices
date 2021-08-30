package com.example.eshop.cart.application.usecases.cartitem;

import com.example.eshop.cart.domain.Cart;
import com.example.eshop.cart.domain.CartRepository;
import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
public class CartItemServiceImpl implements CartItemService {
    private final CartRepository cartRepository;

    public CartItemServiceImpl(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    @Override
    @PreAuthorize("#command.customerId() == principal.getCustomerId()")
    @Transactional
    public void upsert(UpsertCartItemCommand command) {
        var customerId = command.customerId();
        var cart = cartRepository.findByNaturalId(customerId);

        if (cart.isPresent()) {
            upsert(cart.get(), command.ean(), command.quantity());
        } else {
            log.error("Can not find Cart for customer " + customerId);
            throw new RuntimeException("Can not find Cart for customer " + customerId);
        }
    }

    private void upsert(Cart cart, Ean ean, int quantity) {
        if (cart.containsItem(ean)) {
            cart.changeItemQuantity(ean, quantity);
        } else {
            cart.addItem(ean, quantity);
        }
    }
}
