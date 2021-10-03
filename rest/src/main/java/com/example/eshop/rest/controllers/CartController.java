package com.example.eshop.rest.controllers;

import com.example.eshop.cart.application.usecases.cartitemcrud.CartItemCrudService;
import com.example.eshop.cart.application.usecases.cartitemcrud.RemoveCartItemCommand;
import com.example.eshop.cart.application.usecases.cartitemcrud.UpsertCartItemCommand;
import com.example.eshop.cart.application.usecases.cartquery.CartQueryService;
import com.example.eshop.cart.domain.cart.CartItemNotFoundException;
import com.example.eshop.rest.api.CartApi;
import com.example.eshop.rest.controllers.base.BaseController;
import com.example.eshop.rest.controllers.utils.BasicErrorBuilder;
import com.example.eshop.rest.dto.BasicErrorDto;
import com.example.eshop.rest.dto.CartDto;
import com.example.eshop.rest.dto.CheckoutFormDto;
import com.example.eshop.rest.dto.CheckoutRequestDto;
import com.example.eshop.rest.dto.UpsertCartItemCommandDto;
import com.example.eshop.rest.mappers.CartMapper;
import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import java.util.Locale;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Getter(AccessLevel.PROTECTED)  // for access to autowired fields from @ExceptionHandler
public class CartController extends BaseController implements CartApi {
    private final CartItemCrudService cartItemCrudService;
    private final CartQueryService cartQueryService;
    private final CartMapper cartMapper;
    private final MessageSource messageSource;

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    private BasicErrorDto handleCartItemNotFoundException(CartItemNotFoundException e, Locale locale) {
        return BasicErrorBuilder.newInstance()
                .setStatus(HttpStatus.NOT_FOUND)
                .setDetail(getMessageSource().getMessage("cartItemNotFound", new Object[]{ e.getEan() }, locale))
                .build();
    }

    @Override
    public ResponseEntity<CheckoutFormDto> checkout(CheckoutRequestDto checkoutRequestDto) {
        return null;
    }

    @Override
    public ResponseEntity<CartDto> getCart() {
        var cart = getCartForCurrentCustomer();

        return ResponseEntity.ok(cartMapper.toCartDto(cart));
    }

    @Override
    public ResponseEntity<CartDto> removeCartItem(String ean) {
        var userDetails = getUserDetailsOrFail();
        var command = new RemoveCartItemCommand(userDetails.getCustomerId(), Ean.fromString(ean));

        cartItemCrudService.remove(command);

        return getCart();
    }

    @Override
    public ResponseEntity<CartDto> upsertCartItem(UpsertCartItemCommandDto dto) {
        var userDetails = getUserDetailsOrFail();
        var command = new UpsertCartItemCommand(userDetails.getCustomerId(), Ean.fromString(dto.getEan()), dto.getQuantity());

        cartItemCrudService.upsert(command);

        return getCart();
    }

    private com.example.eshop.cart.application.usecases.cartquery.dto.CartDto getCartForCurrentCustomer() {
        var userDetails = getUserDetailsOrFail();

        return cartQueryService.getForCustomer(userDetails.getCustomerId());
    }
}
