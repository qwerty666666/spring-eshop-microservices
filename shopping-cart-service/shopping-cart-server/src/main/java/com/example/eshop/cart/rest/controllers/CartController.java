package com.example.eshop.cart.rest.controllers;

import com.example.eshop.cart.application.services.cartitem.AddCartItemCommand;
import com.example.eshop.cart.application.services.cartitem.AddToCartRuleViolationException;
import com.example.eshop.cart.application.services.cartitem.CartItemService;
import com.example.eshop.cart.application.services.cartitem.NotEnoughQuantityException;
import com.example.eshop.cart.application.services.cartitem.RemoveCartItemCommand;
import com.example.eshop.cart.application.services.cartquery.CartQueryService;
import com.example.eshop.cart.client.api.CartApi;
import com.example.eshop.cart.client.model.AddCartItemCommandDto;
import com.example.eshop.cart.client.model.BasicErrorDto;
import com.example.eshop.cart.client.model.CartDto;
import com.example.eshop.cart.domain.Cart;
import com.example.eshop.cart.domain.CartItemNotFoundException;
import com.example.eshop.cart.rest.mappers.CartMapper;
import com.example.eshop.cart.rest.utils.BasicErrorBuilder;
import com.example.eshop.localizer.Localizer;
import com.example.eshop.sharedkernel.domain.validation.FieldError;
import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import com.example.eshop.sharedkernel.domain.valueobject.InvalidEanFormatException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Getter(AccessLevel.PROTECTED)  // for access to autowired fields from @ExceptionHandler
public class CartController extends BaseController implements CartApi {
    private final CartItemService cartItemService;
    private final CartQueryService cartQueryService;
    private final CartMapper cartMapper;
    private final Localizer localizer;

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    private BasicErrorDto handleCartItemNotFoundException(CartItemNotFoundException e) {
        return BasicErrorBuilder.newInstance()
                .setStatus(HttpStatus.NOT_FOUND)
                .setDetail(getLocalizer().getMessage("cartItemNotFound", e.getEan()))
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    private BasicErrorDto handleAddToCartRuleViolationException(AddToCartRuleViolationException e) {
        String message;

        if (e.getCause() instanceof NotEnoughQuantityException notEnoughQuantityException) {
            message = getLocalizer().getMessage("notEnoughQuantityException", notEnoughQuantityException.getAvailableQuantity());
        } else {
            message = getLocalizer().getMessage("cartItemCantBeAdded");
        }

        return BasicErrorBuilder.newInstance()
                .setStatus(HttpStatus.BAD_REQUEST)
                .setDetail(message)
                .build();
    }

    @Override
    public ResponseEntity<CartDto> getCart() {
        var cart = getCartForAuthenticatedCustomer();

        return ResponseEntity.ok(cartMapper.toCartDto(cart));
    }

    @Override
    @Transactional
    public ResponseEntity<CartDto> removeCartItem(String ean) {
        var command = new RemoveCartItemCommand(getCurrentAuthenticationOrFail().getCustomerId(), convertEanQueryParam(ean));

        cartItemService.remove(command);

        return getCart();
    }

    @Override
    @Transactional
    public ResponseEntity<CartDto> addCartItem(AddCartItemCommandDto dto) {
        var command = convertAddCartItemCommandParam(dto);

        cartItemService.add(command);

        return getCart();
    }

    private Cart getCartForAuthenticatedCustomer() {
        var userDetails = getCurrentAuthenticationOrFail();

        return cartQueryService.getForCustomerOrCreate(userDetails.getCustomerId());
    }

    private Ean convertEanQueryParam(String ean) {
        try {
            return Ean.fromString(ean);
        } catch (InvalidEanFormatException e) {
            throw new InvalidMethodParameterException(new FieldError("ean", "invalidEanFormat", ean));
        }
    }

    private AddCartItemCommand convertAddCartItemCommandParam(AddCartItemCommandDto dto) {
        Ean ean;
        try {
            ean = dto.getEan();
        } catch (InvalidEanFormatException e) {
            throw new InvalidMethodParameterException(new FieldError("ean", "invalidEanFormat", e.getEan()));
        }

        int quantity = dto.getQuantity();
        if (quantity <= 0) {
            throw new InvalidMethodParameterException(new FieldError("quantity", "invalidQuantity"));
        }

        return new AddCartItemCommand(getCurrentAuthenticationOrFail().getCustomerId(), ean, quantity);
    }
}
