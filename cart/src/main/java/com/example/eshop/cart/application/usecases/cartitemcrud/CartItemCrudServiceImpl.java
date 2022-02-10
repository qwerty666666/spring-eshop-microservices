package com.example.eshop.cart.application.usecases.cartitemcrud;

import com.example.eshop.cart.application.usecases.placeorder.ProductNotFoundException;
import com.example.eshop.cart.domain.cart.Cart;
import com.example.eshop.cart.domain.cart.CartRepository;
import com.example.eshop.catalog.client.api.model.Sku;
import com.example.eshop.catalog.client.cataloggateway.CatalogGateway;
import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import com.example.eshop.sharedkernel.domain.valueobject.Money;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

// TODO rename this Service (remove CRUD from the name)
@Component
@RequiredArgsConstructor
@Slf4j
public class CartItemCrudServiceImpl implements CartItemCrudService {
    private final CartRepository cartRepository;
    private final CatalogGateway catalogGateway;

    @Override
    @PreAuthorize("#command.customerId() == authentication.getCustomerId()")
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
        var sku = getSku(ean);
        checkAvailableQuantity(sku, quantity);

        cart.changeItemQuantity(ean, quantity);
    }

    private void addItem(Cart cart, Ean ean, int quantity) {
        var sku = getSku(ean);

        checkAvailableQuantity(sku, quantity);

        cart.addItem(ean, Money.of(sku.getPrice().getAmount(), sku.getPrice().getCurrency()), quantity);
    }

    private void checkAvailableQuantity(Sku sku, int requiredQuantity) {
        if (requiredQuantity > sku.getQuantity()) {
            throw new NotEnoughQuantityException("There are no enough available quantity for " + sku.getEan(),
                    sku.getQuantity(), requiredQuantity);
        }
    }

    @Override
    @PreAuthorize("#command.customerId() == authentication.getCustomerId()")
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

    private Sku getSku(Ean ean) {
        var product = catalogGateway.getProductByEan(ean)
                .blockOptional()
                .orElseThrow(() -> new ProductNotFoundException("Product for EAN " + ean + " does not exist"));

        return product.getSku().stream()
                .filter(s -> s.getEan().equals(ean.toString()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Sku with EAN" + ean + " not found in Product " + product));
    }
}
