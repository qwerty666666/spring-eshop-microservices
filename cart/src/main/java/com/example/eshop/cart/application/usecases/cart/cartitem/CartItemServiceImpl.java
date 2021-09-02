package com.example.eshop.cart.application.usecases.cart.cartitem;

import com.example.eshop.cart.domain.cart.Cart;
import com.example.eshop.cart.domain.cart.CartRepository;
import com.example.eshop.catalog.application.product.ProductCrudService;
import com.example.eshop.catalog.domain.product.Product;
import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
public class CartItemServiceImpl implements CartItemService {
    private final CartRepository cartRepository;
    private final ProductCrudService productCrudService;

    public CartItemServiceImpl(CartRepository cartRepository, ProductCrudService productCrudService) {
        this.cartRepository = cartRepository;
        this.productCrudService = productCrudService;
    }

    @Override
    @PreAuthorize("#command.customerId() == principal.getCustomerId()")
    @Transactional
    public void upsert(UpsertCartItemCommand command) {
        var cart = getCustomerCart(command.customerId());

        var ean = command.ean();
        var quantity = command.quantity();

        if (cart.containsItem(ean)) {
            changeItemQuantity(cart, ean, quantity);
        } else {
            addItem(cart, ean, quantity);
        }
    }

    private void changeItemQuantity(Cart cart, Ean ean, int quantity) {
        cart.changeItemQuantity(ean, quantity);
    }

    private void addItem(Cart cart, Ean ean, int quantity) {
        var product = getProduct(ean);
        var sku = product.getSku(ean);

        cart.addItem(ean, sku.getPrice(), quantity, product.getName());
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

    private Product getProduct(Ean ean) {
        return productCrudService.getByEan(ean);
    }
}
