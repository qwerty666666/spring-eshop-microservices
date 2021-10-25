package com.example.eshop.rest.controllers;

import com.example.eshop.cart.application.usecases.cartquery.CartQueryService;
import com.example.eshop.cart.application.usecases.checkout.CheckoutForm;
import com.example.eshop.cart.application.usecases.checkout.CheckoutProcessService;
import com.example.eshop.cart.application.usecases.checkout.Total;
import com.example.eshop.cart.application.usecases.clearcart.ClearCartService;
import com.example.eshop.cart.application.usecases.placeorder.PlaceOrderService;
import com.example.eshop.cart.domain.cart.Cart;
import com.example.eshop.cart.domain.checkout.order.CreateOrderDto;
import com.example.eshop.cart.domain.checkout.order.DeliveryAddress;
import com.example.eshop.cart.domain.checkout.order.Order;
import com.example.eshop.cart.infrastructure.tests.FakeData;
import com.example.eshop.rest.config.AuthConfig;
import com.example.eshop.rest.config.ControllerTestConfig;
import com.example.eshop.rest.dto.CheckoutRequestDto;
import com.example.eshop.rest.mappers.CheckoutMapper;
import com.example.eshop.sharedkernel.domain.validation.Errors;
import com.example.eshop.sharedkernel.domain.validation.ValidationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CheckoutController.class)
@ActiveProfiles("test")
@Import(ControllerTestConfig.class)
class CheckoutControllerTest {
    @MockBean
    private CartQueryService cartQueryService;
    @MockBean
    private ClearCartService clearCartService;
    @MockBean
    private PlaceOrderService placeOrderService;
    @MockBean
    private CheckoutProcessService checkoutProcessService;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private CheckoutMapper checkoutMapper;

    private final String customerId = AuthConfig.CUSTOMER_ID;
    private final Cart cart = FakeData.cart(customerId);

    private final DeliveryAddress deliveryAddress = FakeData.deliveryAddress();
    private CreateOrderDto createOrderDto;

    @BeforeEach
    void setUp() {
        when(cartQueryService.getForCustomer(eq(customerId))).thenReturn(cart);

        createOrderDto = CreateOrderDto.builder()
                .customerId(customerId)
                .cart(cart)
                .address(deliveryAddress)
                .build();
    }

    @Nested
    class CheckoutProcessTest {
        private CheckoutForm checkoutForm;

        @BeforeEach
        void setUp() {
            checkoutForm = CheckoutForm.builder()
                    .order(new Order(UUID.randomUUID(), customerId, cart, deliveryAddress, null, null))
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

    @Nested
    class PlaceOrderTest {
        @Test
        void givenUnauthorizedRequest_whenPlaceOrder_thenReturn401() throws Exception {
            performPlaceOrderRequest()
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @WithUserDetails(AuthConfig.CUSTOMER_EMAIL)
        void givenInvalidRequest_whenPlaceOrder_thenReturn400() throws Exception {
            when(placeOrderService.place(eq(createOrderDto))).thenThrow(new ValidationException(new Errors()));

            performPlaceOrderRequest()
                    .andExpect(status().isBadRequest());
        }

        @Test
        @WithUserDetails(AuthConfig.CUSTOMER_EMAIL)
        void whenPlaceOrder_thenCartIsClearedAndReturn200() throws Exception {
            var createdOrder = new Order(UUID.randomUUID(), customerId, cart, deliveryAddress, null, null);
            when(placeOrderService.place(createOrderDto)).thenReturn(createdOrder);

            performPlaceOrderRequest()
                    .andExpect(status().isCreated())
                    .andExpect(header().exists(HttpHeaders.LOCATION));

            verify(placeOrderService).place(eq(createOrderDto));
            verify(clearCartService).clear(eq(customerId));
        }

        private ResultActions performPlaceOrderRequest() throws Exception {
            var checkoutRequestDto = new CheckoutRequestDto();
            checkoutRequestDto.setAddress(checkoutMapper.toDeliveryAddressDto(deliveryAddress));

            var json = objectMapper.writeValueAsString(checkoutRequestDto);

            return mockMvc.perform(post("/api/cart/checkout/confirm/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json)
            );
        }
    }
}
