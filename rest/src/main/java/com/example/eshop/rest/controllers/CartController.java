package com.example.eshop.rest.controllers;

import com.example.eshop.cart.application.usecases.cartitemcrud.AddCartItemCommand;
import com.example.eshop.cart.application.usecases.cartitemcrud.CartItemCrudService;
import com.example.eshop.cart.application.usecases.cartitemcrud.NotEnoughQuantityException;
import com.example.eshop.cart.application.usecases.cartitemcrud.RemoveCartItemCommand;
import com.example.eshop.cart.application.usecases.cartquery.CartQueryService;
import com.example.eshop.cart.domain.cart.Cart;
import com.example.eshop.cart.domain.cart.CartItemNotFoundException;
import com.example.eshop.localizer.Localizer;
import com.example.eshop.rest.api.CartApi;
import com.example.eshop.rest.controllers.base.BaseController;
import com.example.eshop.rest.controllers.base.BasicErrorBuilder;
import com.example.eshop.rest.dto.AddCartItemCommandDto;
import com.example.eshop.rest.dto.BasicErrorDto;
import com.example.eshop.rest.dto.CartDto;
import com.example.eshop.rest.mappers.CartMapper;
import com.example.eshop.rest.utils.UriUtils;
import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import java.util.Locale;

@RestController
@RequestMapping(UriUtils.API_BASE_PATH_PROPERTY)
@RequiredArgsConstructor
@Getter(AccessLevel.PROTECTED)  // for access to autowired fields from @ExceptionHandler
public class CartController extends BaseController implements CartApi {
    private final CartItemCrudService cartItemCrudService;
    private final CartQueryService cartQueryService;
    private final CartMapper cartMapper;

    private final Localizer localizer;

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    private BasicErrorDto handleCartItemNotFoundException(CartItemNotFoundException e, Locale locale) {
        return BasicErrorBuilder.newInstance()
                .setStatus(HttpStatus.NOT_FOUND)
                .setDetail(getLocalizer().getMessage("cartItemNotFound", e.getEan()))
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    private BasicErrorDto handleNException(NotEnoughQuantityException e) {
        return BasicErrorBuilder.newInstance()
                .setStatus(HttpStatus.BAD_REQUEST)
                .setDetail(getLocalizer().getMessage("notEnoughQuantityException", e.getAvailableQuantity()))
                .build();
    }

    @Override
    public ResponseEntity<CartDto> getCart() {
        var cart = getCartForAuthenticatedCustomer();

        return ResponseEntity.ok(cartMapper.toCartDto(cart));
    }

    @Override
    public ResponseEntity<CartDto> removeCartItem(String ean) {
        var userDetails = getCurrentAuthenticationOrFail();
        var command = new RemoveCartItemCommand(userDetails.getCustomerId(), Ean.fromString(ean));

        cartItemCrudService.remove(command);

        return getCart();
    }

    @Override
    public ResponseEntity<CartDto> addCartItem(AddCartItemCommandDto dto) {
        var userDetails = getCurrentAuthenticationOrFail();
        var command = new AddCartItemCommand(userDetails.getCustomerId(), Ean.fromString(dto.getEan()), dto.getQuantity());

        cartItemCrudService.add(command);

        return getCart();
    }

    private Cart getCartForAuthenticatedCustomer() {
        var userDetails = getCurrentAuthenticationOrFail();

        return cartQueryService.getForCustomer(userDetails.getCustomerId());
    }
}
