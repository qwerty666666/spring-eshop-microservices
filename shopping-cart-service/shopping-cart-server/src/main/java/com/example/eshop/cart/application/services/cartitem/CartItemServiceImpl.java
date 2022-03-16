package com.example.eshop.cart.application.services.cartitem;

import com.example.eshop.cart.application.services.cartquery.CartQueryService;
import com.example.eshop.cart.domain.Cart;
import com.example.eshop.catalog.client.CatalogService;
import com.example.eshop.catalog.client.model.SkuWithProductDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class CartItemServiceImpl implements CartItemService {
    private final CatalogService catalogService;
    private final CartQueryService cartQueryService;
    private AddCartItemRule addCartItemRule = new AvailableQuantityRule();

    public CartItemServiceImpl setAddCartItemRule(AddCartItemRule addCartItemRule) {
        this.addCartItemRule = addCartItemRule;
        return this;
    }

    @Override
    @PreAuthorize("#command.customerId() == authentication.getCustomerId()")
    @Transactional
    public void add(AddCartItemCommand command) {
        var cart = getCustomerCart(command.customerId());

        if (cart.containsItem(command.ean())) {
            changeItemQuantity(cart, command);
        } else {
            addItem(cart, command);
        }
    }

    /**
     * @throws AddToCartRuleViolationException if quantity can't be changed
     */
    private void changeItemQuantity(Cart cart, AddCartItemCommand command) {
        var sku = getSku(command);

        checkIfItemCanBeAdded(cart, command, sku);

        cart.changeItemQuantity(command.ean(), command.quantity());
    }

    /**
     * @throws AddToCartRuleViolationException if item can't be added
     */
    private void addItem(Cart cart, AddCartItemCommand command) {
        var sku = getSku(command);

        checkIfItemCanBeAdded(cart, command, sku);

        cart.addItem(command.ean(), sku.getPrice(), command.quantity());
    }

    /**
     * Checks if the given item can be added to the cart
     *
     * @throws AddToCartRuleViolationException if item can be added
     */
    private void checkIfItemCanBeAdded(Cart cart, AddCartItemCommand command, SkuWithProductDto sku) {
        addCartItemRule.check(cart, command, sku);
    }

    @Override
    @PreAuthorize("#command.customerId() == authentication.getCustomerId()")
    @Transactional
    public void remove(RemoveCartItemCommand command) {
        var cart = getCustomerCart(command.customerId());

        cart.removeItem(command.ean());
    }

    private Cart getCustomerCart(String customerId) {
        return cartQueryService.getForCustomerOrCreate(customerId);
    }

    private SkuWithProductDto getSku(AddCartItemCommand command) {
        return catalogService.getSku(command.ean())
                .blockOptional()
                .orElseThrow(() -> new ProductNotFoundException("Sku for EAN " + command.ean() + " does not exist"));
    }
}
