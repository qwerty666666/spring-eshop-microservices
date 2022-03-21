package com.example.eshop.rest.controllers;

import com.example.eshop.cart.application.usecases.checkout.CheckoutProcessService;
import com.example.eshop.cart.application.usecases.clearcart.ClearCartService;
import com.example.eshop.cart.application.usecases.placeorder.PlaceOrderUsecase;
import com.example.eshop.cart.client.CartServiceClient;
import com.example.eshop.cart.client.model.CartDto;
import com.example.eshop.cart.domain.checkout.order.CreateOrderDto;
import com.example.eshop.localizer.Localizer;
import com.example.eshop.rest.api.CheckoutApi;
import com.example.eshop.rest.controllers.base.BaseController;
import com.example.eshop.rest.dto.CheckoutFormDto;
import com.example.eshop.rest.dto.CheckoutRequestDto;
import com.example.eshop.rest.mappers.CheckoutMapper;
import com.example.eshop.rest.models.ValidationErrorDto;
import com.example.eshop.rest.utils.UriUtils;
import com.example.eshop.sharedkernel.domain.validation.ValidationException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import java.time.Duration;

@RestController
@RequestMapping(UriUtils.API_BASE_PATH_PROPERTY)
@RequiredArgsConstructor
@Getter(AccessLevel.PROTECTED)  // for access to autowired fields from @ExceptionHandler
public class CheckoutController extends BaseController implements CheckoutApi {
    private final CartServiceClient cartServiceClient;
    private final ClearCartService clearCartService;

    private final CheckoutProcessService checkoutProcessService;
    private final PlaceOrderUsecase placeOrderUsecase;
    private final CheckoutMapper checkoutMapper;

    private final UriUtils uriUtils;
    private final Localizer localizer;

    /**
     * Handle exception from Domain Validation
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    private ValidationErrorDto onValidationException(ValidationException e) {
        var errors = new ValidationErrorDto();

        for (var err: e.getErrors()) {
            errors.addError(err.getField(), localizer.getMessage(err.getMessageCode(), err.getMessageParams()));
        }

        return errors;
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
        var order = placeOrderUsecase.place(createOrderDto);

        // clear customer's cart
        clearCartService.clear(getCurrentAuthenticationOrFail().getCustomerId());

        // and return Location to created order
        var location = uriUtils.buildOrderUri(order.getId());

        return ResponseEntity.created(location).build();
    }

    private CreateOrderDto buildCreateOrderDto(CheckoutRequestDto checkoutRequestDto) {
        var customerId = getCurrentAuthenticationOrFail().getCustomerId();
        var cart = getCartForAuthenticatedCustomer();

        return checkoutMapper.toCreateOrderDto(checkoutRequestDto, customerId, cart);
    }

    private CartDto getCartForAuthenticatedCustomer() {
        var customerId = getCurrentAuthenticationOrFail().getCustomerId();

        return cartServiceClient.getCart(customerId)
                .block(Duration.ofSeconds(5));
    }
}
