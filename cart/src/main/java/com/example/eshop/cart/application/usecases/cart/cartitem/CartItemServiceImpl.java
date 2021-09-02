package com.example.eshop.cart.application.usecases.cart.cartitem;

import com.example.eshop.cart.domain.cart.Cart;
import com.example.eshop.cart.domain.cart.CartRepository;
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
        var cart = getCustomerCart(command.customerId());

        var ean = command.ean();
        var quantity = command.quantity();

        if (cart.containsItem(ean)) {
            cart.changeItemQuantity(ean, quantity);
        } else {
            cart.addItem(ean, quantity);
        }
    }

    @Override
    @PreAuthorize("#command.customerId() == principal.getCustomerId()")
    @Transactional
    public void remove(RemoveCartItemCommand command) {
        var cart = getCustomerCart(command.customerId());

        cart.removeItem(command.ean());
    }

    private Cart getCustomerCart(String customerId) {
        var cart = cartRepository.findByNaturalId(customerId);

        if (cart.isEmpty()) {
            log.error("Can not find Cart for customer " + customerId);
            throw new RuntimeException("Can not find Cart for customer " + customerId);
        }

        return cart.get();
    }
}
