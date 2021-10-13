package com.example.eshop.rest.controllers;

import com.example.eshop.cart.application.usecases.cartitemcrud.CartItemCrudService;
import com.example.eshop.cart.application.usecases.cartitemcrud.RemoveCartItemCommand;
import com.example.eshop.cart.application.usecases.cartitemcrud.AddCartItemCommand;
import com.example.eshop.cart.application.usecases.cartquery.CartQueryService;
import com.example.eshop.cart.application.usecases.checkout.CheckoutProcessService;
import com.example.eshop.cart.application.usecases.clearcart.ClearCartService;
import com.example.eshop.cart.application.usecases.placeorder.PlaceOrderService;
import com.example.eshop.cart.domain.cart.Cart;
import com.example.eshop.cart.domain.cart.CartItemNotFoundException;
import com.example.eshop.cart.domain.checkout.order.CreateOrderDto;
import com.example.eshop.rest.api.CartApi;
import com.example.eshop.rest.controllers.base.BaseController;
import com.example.eshop.rest.controllers.utils.BasicErrorBuilder;
import com.example.eshop.rest.dto.AddCartItemCommandDto;
import com.example.eshop.rest.dto.BasicErrorDto;
import com.example.eshop.rest.dto.CartDto;
import com.example.eshop.rest.dto.CheckoutFormDto;
import com.example.eshop.rest.dto.CheckoutRequestDto;
import com.example.eshop.rest.mappers.CartMapper;
import com.example.eshop.rest.mappers.CheckoutMapper;
import com.example.eshop.rest.utils.UriBuilder;
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
    private final ClearCartService clearCartService;
    private final CartMapper cartMapper;

    private final CheckoutProcessService checkoutProcessService;
    private final PlaceOrderService placeOrderService;
    private final CheckoutMapper checkoutMapper;

    private final MessageSource messageSource;
    private final UriBuilder uriBuilder;

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
        var createOrderDto = buildCreateOrderDto(checkoutRequestDto);

        var form = checkoutProcessService.process(createOrderDto);

        return ResponseEntity.ok(checkoutMapper.toCheckoutFormDto(form));
    }

    @Override
    public ResponseEntity<Void> placeOrder(CheckoutRequestDto checkoutRequestDto) {
        var createOrderDto = buildCreateOrderDto(checkoutRequestDto);

        // place order
        var order = placeOrderService.place(createOrderDto);

        // clear customer's cart
        clearCartService.clear(getAuthenticatedUserDetailsOrFail().getCustomerId());

        // and return Location to created order
        var location = uriBuilder.buildOrderUri(order.getId());

        return ResponseEntity.created(location).build();
    }

    @Override
    public ResponseEntity<CartDto> getCart() {
        var cart = getCartForAuthenticatedCustomer();

        return ResponseEntity.ok(cartMapper.toCartDto(cart));
    }

    @Override
    public ResponseEntity<CartDto> removeCartItem(String ean) {
        var userDetails = getAuthenticatedUserDetailsOrFail();
        var command = new RemoveCartItemCommand(userDetails.getCustomerId(), Ean.fromString(ean));

        cartItemCrudService.remove(command);

        return getCart();
    }

    @Override
    public ResponseEntity<CartDto> addCartItem(AddCartItemCommandDto dto) {
        var userDetails = getAuthenticatedUserDetailsOrFail();
        var command = new AddCartItemCommand(userDetails.getCustomerId(), Ean.fromString(dto.getEan()), dto.getQuantity());

        cartItemCrudService.add(command);

        return getCart();
    }

    private Cart getCartForAuthenticatedCustomer() {
        var userDetails = getAuthenticatedUserDetailsOrFail();

        return cartQueryService.getForCustomer(userDetails.getCustomerId());
    }

    private CreateOrderDto buildCreateOrderDto(CheckoutRequestDto checkoutRequestDto) {
        var customerId = getAuthenticatedUserDetailsOrFail().getCustomerId();
        var cart = getCartForAuthenticatedCustomer();

        return checkoutMapper.toOrderDto(checkoutRequestDto, customerId, cart);
    }
}
