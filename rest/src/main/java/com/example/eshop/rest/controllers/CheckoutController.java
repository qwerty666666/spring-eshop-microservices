package com.example.eshop.rest.controllers;

import com.example.eshop.cart.application.usecases.cartquery.CartQueryService;
import com.example.eshop.cart.application.usecases.checkout.CheckoutProcessService;
import com.example.eshop.cart.application.usecases.clearcart.ClearCartService;
import com.example.eshop.cart.application.usecases.placeorder.PlaceOrderService;
import com.example.eshop.cart.domain.cart.Cart;
import com.example.eshop.cart.domain.checkout.order.CreateOrderDto;
import com.example.eshop.rest.api.CheckoutApi;
import com.example.eshop.rest.controllers.base.BaseController;
import com.example.eshop.rest.dto.CheckoutFormDto;
import com.example.eshop.rest.dto.CheckoutRequestDto;
import com.example.eshop.rest.mappers.CheckoutMapper;
import com.example.eshop.rest.utils.UriFactory;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(UriFactory.API_BASE_PATH_PROPERTY)
@RequiredArgsConstructor
@Getter(AccessLevel.PROTECTED)  // for access to autowired fields from @ExceptionHandler
public class CheckoutController extends BaseController implements CheckoutApi {
    private final CartQueryService cartQueryService;
    private final ClearCartService clearCartService;

    private final CheckoutProcessService checkoutProcessService;
    private final PlaceOrderService placeOrderService;
    private final CheckoutMapper checkoutMapper;

    private final UriFactory uriFactory;

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
        var location = uriFactory.buildOrderUri(order.getId());

        return ResponseEntity.created(location).build();
    }

    private CreateOrderDto buildCreateOrderDto(CheckoutRequestDto checkoutRequestDto) {
        var customerId = getAuthenticatedUserDetailsOrFail().getCustomerId();
        var cart = getCartForAuthenticatedCustomer();

        return checkoutMapper.toOrderDto(checkoutRequestDto, customerId, cart);
    }

    private Cart getCartForAuthenticatedCustomer() {
        var userDetails = getAuthenticatedUserDetailsOrFail();

        return cartQueryService.getForCustomer(userDetails.getCustomerId());
    }
}
