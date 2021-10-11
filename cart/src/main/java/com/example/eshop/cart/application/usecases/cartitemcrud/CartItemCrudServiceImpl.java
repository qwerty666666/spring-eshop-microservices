package com.example.eshop.cart.application.usecases.cartitemcrud;

import com.example.eshop.cart.domain.cart.Cart;
import com.example.eshop.cart.domain.cart.CartRepository;
import com.example.eshop.catalog.application.product.ProductCrudService;
import com.example.eshop.catalog.domain.product.Product;
import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class CartItemCrudServiceImpl implements CartItemCrudService {
    private final CartRepository cartRepository;
    private final ProductCrudService productCrudService;

    @Override
    @PreAuthorize("#command.customerId() == principal.getCustomerId()")
    @Transactional
    public void add(AddCartItemCommand command) {
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
        return cartRepository.findByNaturalId(customerId)
                .orElseThrow(() -> {
                    log.error("Can not find Cart for customer " + customerId);
                    return new RuntimeException("Can not find Cart for customer " + customerId);
                });
    }

    private Product getProduct(Ean ean) {
        return productCrudService.getByEan(ean);
    }
}
