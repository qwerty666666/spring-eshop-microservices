package com.example.eshop.rest.controllers;

import com.example.eshop.cart.application.usecases.cartitemcrud.CartItemCrudService;
import com.example.eshop.cart.application.usecases.cartitemcrud.RemoveCartItemCommand;
import com.example.eshop.cart.application.usecases.cartitemcrud.UpsertCartItemCommand;
import com.example.eshop.cart.application.usecases.cartquery.CartQueryService;
import com.example.eshop.cart.domain.cart.CartItemNotFoundException;
import com.example.eshop.customer.infrastructure.auth.UserDetailsImpl;
import com.example.eshop.rest.mappers.CartMapper;
import com.example.eshop.rest.requests.PutItemToCartRequest;
import com.example.eshop.rest.resources.cart.CartResource;
import com.example.eshop.rest.resources.shared.ErrorResponse;
import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;
import java.util.Locale;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    @Autowired
    private CartItemCrudService cartItemCrudService;

    @Autowired
    private CartQueryService cartQueryService;

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private MessageSource messageSource;

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> handleCartItemNotFoundException(CartItemNotFoundException e, Locale locale) {
        var message = messageSource.getMessage("cartItemNotFound", new Object[]{ e.getEan() }, locale);

        return new ResponseEntity<>(
                new ErrorResponse(HttpStatus.NOT_FOUND.value(), message),
                HttpStatus.NOT_FOUND
        );
    }

    /**
     * Get Cart for the authenticated customer
     */
    @GetMapping("")
    public CartResource getCart(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return getCartForCurrentCustomer(userDetails);
    }

    /**
     * Add CartItem to the authenticated customer's cart, or change CartItem quantity
     */
    @PutMapping("/items")
    public CartResource putCartItem(@Valid @RequestBody PutItemToCartRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        var command = new UpsertCartItemCommand(userDetails.getCustomerId(), request.ean(), request.quantity());
        cartItemCrudService.upsert(command);

        return getCartForCurrentCustomer(userDetails);
    }

    /**
     * Remove CartItem from the authenticated customer's cart
     */
    @DeleteMapping("/items/{ean}")
    public Object removeCartItem(@PathVariable Ean ean, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        var command = new RemoveCartItemCommand(userDetails.getCustomerId(), ean);
        cartItemCrudService.remove(command);

        return getCartForCurrentCustomer(userDetails);
    }

    private CartResource getCartForCurrentCustomer(UserDetailsImpl userDetails) {
        var cart = cartQueryService.getForCustomer(userDetails.getCustomerId());
        return cartMapper.toCartResource(cart);
    }
}
