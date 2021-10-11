package com.example.eshop.rest.controllers;

import com.example.eshop.cart.application.usecases.cartitemcrud.AddCartItemCommand;
import com.example.eshop.cart.application.usecases.cartitemcrud.CartItemCrudService;
import com.example.eshop.cart.application.usecases.cartitemcrud.RemoveCartItemCommand;
import com.example.eshop.cart.application.usecases.cartquery.CartQueryService;
import com.example.eshop.cart.application.usecases.checkout.CheckoutForm;
import com.example.eshop.cart.application.usecases.checkout.CheckoutProcessService;
import com.example.eshop.cart.application.usecases.checkout.Total;
import com.example.eshop.cart.domain.cart.Cart;
import com.example.eshop.cart.domain.cart.CartItemNotFoundException;
import com.example.eshop.cart.domain.checkout.order.CreateOrderDto;
import com.example.eshop.cart.domain.checkout.order.DeliveryAddress;
import com.example.eshop.cart.domain.checkout.order.Order;
import com.example.eshop.cart.infrastructure.tests.FakeData;
import com.example.eshop.rest.config.AuthConfig;
import com.example.eshop.rest.config.ControllerTestConfig;
import com.example.eshop.rest.dto.CheckoutRequestDto;
import com.example.eshop.rest.mappers.CartMapper;
import com.example.eshop.rest.mappers.CheckoutMapper;
import com.example.eshop.sharedkernel.domain.validation.Errors;
import com.example.eshop.sharedkernel.domain.validation.ValidationException;
import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import com.example.eshop.sharedkernel.domain.valueobject.Money;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CartController.class)
@ActiveProfiles("test")
@Import(ControllerTestConfig.class)
class CartControllerTest {
    private final static Ean EAN = FakeData.ean();
    private final static Money PRICE = Money.USD(12.34);
    private final static int QUANTITY = 7;
    private final static String PRODUCT_NAME = "Test Product";

    @MockBean
    private CartQueryService cartQueryService;
    @MockBean
    private CartItemCrudService cartItemCrudService;
    @MockBean
    private CheckoutProcessService checkoutProcessService;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private CheckoutMapper checkoutMapper;

    private Cart cart;
    private final String customerId = AuthConfig.CUSTOMER_ID;

    @BeforeEach
    void setUp() {
        cart = new Cart(customerId);
        cart.addItem(EAN, PRICE, QUANTITY, PRODUCT_NAME);

        when(cartQueryService.getForCustomer(eq(customerId))).thenReturn(cart);
    }

    @Nested
    class GetCartTest {
        @Test
        @WithUserDetails(AuthConfig.CUSTOMER_EMAIL)
        void whenGetCart_thenReturnCartForTheAuthenticatedCustomer() throws Exception {
            var expectedJson = objectMapper.writeValueAsString(cartMapper.toCartDto(cart));

            performGetCartRequest()
                    .andExpect(status().isOk())
                    .andExpect(content().json(expectedJson));

            verify(cartQueryService).getForCustomer(AuthConfig.CUSTOMER_ID);
        }

        @Test
        void givenUnauthorizedRequest_whenGetCart_thenReturn401() throws Exception {
            performGetCartRequest()
                    .andExpect(status().isUnauthorized());
        }

        private ResultActions performGetCartRequest() throws Exception {
            return mockMvc.perform(get("/api/cart"));
        }
    }

    @Nested
    class PutItemTest {
        @Test
        @WithUserDetails(AuthConfig.CUSTOMER_EMAIL)
        void whenPutCartItem_thenCartItemServiceUpsertIsCalledAndCartIsReturned() throws Exception {
            var expectedJson = objectMapper.writeValueAsString(cartMapper.toCartDto(cart));
            var expectedCommand = new AddCartItemCommand(AuthConfig.CUSTOMER_ID, EAN, QUANTITY);

            performPutCartItemRequest()
                    .andExpect(status().isOk())
                    .andExpect(content().json(expectedJson));

            verify(cartItemCrudService).add(eq(expectedCommand));
        }

        @Test
        void givenUnauthorizedRequest_whenPutCartItem_thenReturn401() throws Exception {
            performPutCartItemRequest()
                    .andExpect(status().isUnauthorized());
        }

        private ResultActions performPutCartItemRequest() throws Exception {
            var json = """
                    {
                        "ean": "%s",
                        "quantity": %d
                    }
                    """.formatted(EAN, QUANTITY);

            return mockMvc.perform(put("/api/cart/items")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json));
        }
    }

    @Nested
    class RemoveCartItemTest {
        @Test
        @WithUserDetails(AuthConfig.CUSTOMER_EMAIL)
        void whenRemoveCartItem_thenCartItemServiceRemoveIsCalledAndCartIsReturned() throws Exception {
            var expectedJson = objectMapper.writeValueAsString(cartMapper.toCartDto(cart));
            var expectedCommand = new RemoveCartItemCommand(AuthConfig.CUSTOMER_ID, EAN);

            performRemoveCartItemRequest()
                    .andExpect(status().isOk())
                    .andExpect(content().json(expectedJson));

            verify(cartItemCrudService).remove(eq(expectedCommand));
        }

        @Test
        @WithUserDetails(AuthConfig.CUSTOMER_EMAIL)
        void givenNonExistingEan_whenRemoveCartItem_thenReturn404() throws Exception {
            var expectedCommand = new RemoveCartItemCommand(AuthConfig.CUSTOMER_ID, EAN);

            doThrow(CartItemNotFoundException.class).when(cartItemCrudService).remove(expectedCommand);

            performRemoveCartItemRequest()
                    .andExpect(status().isNotFound());

            verify(cartItemCrudService).remove(eq(expectedCommand));
        }

        @Test
        void givenUnauthorizedRequest_whenRemoveCartItem_thenReturn401() throws Exception {
            performRemoveCartItemRequest()
                    .andExpect(status().isUnauthorized());
        }

        private ResultActions performRemoveCartItemRequest() throws Exception {
            return mockMvc.perform(delete("/api/cart/items/" + EAN));
        }
    }

    @Nested
    class CheckoutTest {
        private final DeliveryAddress deliveryAddress = FakeData.deliveryAddress();
        private CreateOrderDto createOrderDto;
        private CheckoutForm checkoutForm;

        @BeforeEach
        void setUp() {
            createOrderDto = CreateOrderDto.builder()
                    .customerId(customerId)
                    .cart(cart)
                    .address(deliveryAddress)
                    .build();

            checkoutForm = CheckoutForm.builder()
                    .order(new Order(customerId, cart, deliveryAddress, null, null))
                    .availableDeliveries(Collections.emptyList())
                    .availablePayments(Collections.emptyList())
                    .total(new Total(cart, null))
                    .build();
        }

        @Test
        void givenUnauthorizedRequest_whenCheckout_thenReturn401() throws Exception {
            performCheckoutRequest()
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @WithUserDetails(AuthConfig.CUSTOMER_EMAIL)
        void givenInvalidRequest_whenCheckout_thenReturn400() throws Exception {
            when(checkoutProcessService.process(eq(createOrderDto)))
                    .thenThrow(new ValidationException(new Errors()));

            performCheckoutRequest()
                    .andExpect(status().isBadRequest());
        }

        @Test
        @WithUserDetails(AuthConfig.CUSTOMER_EMAIL)
        void whenCheckout_thenReturnCheckout() throws Exception {
            // Given
            when(checkoutProcessService.process(eq(createOrderDto))).thenReturn(checkoutForm);

            var expectedJson = objectMapper.writeValueAsString(checkoutMapper.toCheckoutFormDto(checkoutForm));

            // When + then
            performCheckoutRequest()
                    .andExpect(status().isOk())
                    .andExpect(content().json(expectedJson));

            verify(checkoutProcessService).process(eq(createOrderDto));
        }

        private ResultActions performCheckoutRequest() throws Exception {
            var checkoutRequestDto = new CheckoutRequestDto();
            checkoutRequestDto.setAddress(checkoutMapper.toDeliveryAddressDto(deliveryAddress));

            var json = objectMapper.writeValueAsString(checkoutRequestDto);

            return mockMvc.perform(post("/api/cart/checkout/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json)
            );
        }
    }
}
