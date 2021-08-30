package com.example.eshop.rest.controllers;

import com.example.eshop.cart.application.usecases.cartitem.CartItemService;
import com.example.eshop.cart.application.usecases.cartitem.UpsertCartItemCommand;
import com.example.eshop.cart.application.usecases.query.CartQueryService;
import com.example.eshop.customer.infrastructure.auth.UserDetailsImpl;
import com.example.eshop.rest.mappers.CartMapper;
import com.example.eshop.rest.requests.PutItemToCartRequest;
import com.example.eshop.rest.resources.cart.CartResource;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    private final CartItemService cartItemService;
    private final CartQueryService cartQueryService;
    private final CartMapper cartMapper;

    public CartController(CartItemService cartItemService, CartQueryService cartQueryService, CartMapper cartMapper) {
        this.cartItemService = cartItemService;
        this.cartQueryService = cartQueryService;
        this.cartMapper = cartMapper;
    }

    @GetMapping("")
    public CartResource getCart(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return getCartForCurrentCustomer(userDetails);
    }

    @PutMapping("/items")
    public CartResource putCartItem(@Valid @RequestBody PutItemToCartRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        var command = new UpsertCartItemCommand(userDetails.getCustomerId(), request.ean(), request.quantity());
        cartItemService.upsert(command);

        return getCartForCurrentCustomer(userDetails);
    }

    private CartResource getCartForCurrentCustomer(UserDetailsImpl userDetails) {
        var cart = cartQueryService.getForCustomer(userDetails.getCustomerId());
        return cartMapper.toCartResource(cart);
    }
}
