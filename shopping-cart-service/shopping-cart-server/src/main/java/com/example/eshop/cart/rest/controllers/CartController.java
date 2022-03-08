package com.example.eshop.cart.rest.controllers;

import com.example.eshop.cart.application.usecases.cartitemcrud.AddCartItemCommand;
import com.example.eshop.cart.application.usecases.cartitemcrud.CartItemCrudService;
import com.example.eshop.cart.application.usecases.cartitemcrud.NotEnoughQuantityException;
import com.example.eshop.cart.application.usecases.cartitemcrud.RemoveCartItemCommand;
import com.example.eshop.cart.application.usecases.cartquery.CartQueryService;
import com.example.eshop.cart.client.api.model.AddCartItemCommandDto;
import com.example.eshop.cart.client.api.model.BasicErrorDto;
import com.example.eshop.cart.client.api.model.CartDto;
import com.example.eshop.cart.domain.Cart;
import com.example.eshop.cart.domain.CartItemNotFoundException;
import com.example.eshop.cart.rest.api.CartApi;
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
    private final CartItemCrudService cartItemCrudService;
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
    private BasicErrorDto handleNotEnoughQuantityException(NotEnoughQuantityException e) {
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
    @Transactional
    public ResponseEntity<CartDto> removeCartItem(String ean) {
        var command = new RemoveCartItemCommand(getCurrentAuthenticationOrFail().getCustomerId(), convertEanQueryParam(ean));

        cartItemCrudService.remove(command);

        return getCart();
    }

    @Override
    @Transactional
    public ResponseEntity<CartDto> addCartItem(AddCartItemCommandDto dto) {
        var command = convertAddCartItemCommandParam(dto);

        cartItemCrudService.add(command);

        return getCart();
    }

    private Cart getCartForAuthenticatedCustomer() {
        var userDetails = getCurrentAuthenticationOrFail();

        return cartQueryService.getForCustomer(userDetails.getCustomerId());
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
            ean = Ean.fromString(dto.getEan());
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
