package com.example.eshop.rest.controllers;

import com.example.eshop.auth.WithMockCustomJwtAuthentication;
import com.example.eshop.cart.application.usecases.cartquery.CartQueryService;
import com.example.eshop.cart.application.usecases.checkout.CheckoutForm;
import com.example.eshop.cart.application.usecases.checkout.CheckoutProcessService;
import com.example.eshop.cart.application.usecases.checkout.Total;
import com.example.eshop.cart.application.usecases.clearcart.ClearCartService;
import com.example.eshop.cart.application.usecases.placeorder.PlaceOrderUsecase;
import com.example.eshop.cart.domain.cart.Cart;
import com.example.eshop.cart.domain.checkout.order.CreateOrderDto;
import com.example.eshop.cart.domain.checkout.order.DeliveryAddress;
import com.example.eshop.cart.domain.checkout.order.Order;
import com.example.eshop.cart.infrastructure.tests.FakeData;
import com.example.eshop.rest.config.AuthConfig;
import com.example.eshop.rest.config.ControllerTest;
import com.example.eshop.rest.dto.CheckoutFormDto;
import com.example.eshop.rest.dto.CheckoutRequestDto;
import com.example.eshop.rest.dto.DeliveryAddressDto;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import java.util.Collections;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CheckoutController.class)
@ControllerTest
class CheckoutControllerTest {
    @MockBean
    private CartQueryService cartQueryService;
    @MockBean
    private ClearCartService clearCartService;
    @MockBean
    private PlaceOrderUsecase placeOrderUsecase;
    @MockBean
    private CheckoutProcessService checkoutProcessService;
    @MockBean
    private CheckoutMapper checkoutMapper;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private final String customerId = AuthConfig.CUSTOMER_ID;
    private final Cart cart = FakeData.cart(customerId);

    private final DeliveryAddress deliveryAddress = FakeData.deliveryAddress();
    private final CheckoutRequestDto checkoutRequestDto = new CheckoutRequestDto()
            .address(new DeliveryAddressDto()
                    .country(deliveryAddress.country())
                    .city(deliveryAddress.city())
                    .street(deliveryAddress.street())
                    .building(deliveryAddress.building())
                    .flat(deliveryAddress.flat())
                    .fullname(deliveryAddress.fullname())
                    .phone(deliveryAddress.phone().toString())
            );
    private final CreateOrderDto createOrderDto = CreateOrderDto.builder()
            .customerId(customerId)
            .cart(cart)
            .address(deliveryAddress)
            .build();

    @BeforeEach
    void setUp() {
        when(cartQueryService.getForCustomer(customerId)).thenReturn(cart);
        when(checkoutMapper.toCreateOrderDto(checkoutRequestDto, customerId, cart)).thenReturn(createOrderDto);
    }

    @Nested
    class CheckoutProcessTest {
        private Order order;
        private CheckoutForm checkoutForm;
        private CheckoutFormDto checkoutFormDto;

        @BeforeEach
        void setUp() {
            order = new Order(UUID.randomUUID(), customerId, cart, deliveryAddress, null, null);
            checkoutForm = CheckoutForm.builder()
                    .order(order)
                    .availableDeliveries(Collections.emptyList())
                    .availablePayments(Collections.emptyList())
                    .build();
            checkoutFormDto = new CheckoutFormDto();

            when(checkoutMapper.toCheckoutFormDto(checkoutForm)).thenReturn(checkoutFormDto);
        }

        @Test
        void givenUnauthorizedRequest_whenCheckout_thenReturn401() throws Exception {
            performCheckoutRequest()
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockCustomJwtAuthentication(customerId = AuthConfig.CUSTOMER_ID)
        void givenInvalidRequest_whenCheckout_thenReturn400() throws Exception {
            when(checkoutProcessService.process(createOrderDto))
                    .thenThrow(new ValidationException(new Errors()));

            performCheckoutRequest()
                    .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockCustomJwtAuthentication(customerId = AuthConfig.CUSTOMER_ID)
        void whenCheckout_thenReturnCheckout() throws Exception {
            // Given
            when(checkoutProcessService.process(createOrderDto)).thenReturn(checkoutForm);

            var expectedJson = objectMapper.writeValueAsString(checkoutFormDto);

            // When + then
            performCheckoutRequest()
                    .andExpect(status().isOk())
                    .andExpect(content().json(expectedJson));

            verify(checkoutProcessService).process(createOrderDto);
            verify(checkoutMapper).toCheckoutFormDto(checkoutForm);
        }

        private ResultActions performCheckoutRequest() throws Exception {
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
        @WithMockCustomJwtAuthentication(customerId = AuthConfig.CUSTOMER_ID)
        void givenInvalidRequest_whenPlaceOrder_thenReturn400() throws Exception {
            when(placeOrderUsecase.place(createOrderDto)).thenThrow(new ValidationException(new Errors()));

            performPlaceOrderRequest()
                    .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockCustomJwtAuthentication(customerId = AuthConfig.CUSTOMER_ID)
        void whenPlaceOrder_thenCartIsClearedAndReturn200() throws Exception {
            var createdOrder = new Order(UUID.randomUUID(), customerId, cart, deliveryAddress, null, null);
            when(placeOrderUsecase.place(createOrderDto)).thenReturn(createdOrder);

            performPlaceOrderRequest()
                    .andExpect(status().isCreated())
                    .andExpect(header().exists(HttpHeaders.LOCATION));

            verify(placeOrderUsecase).place(createOrderDto);
            verify(clearCartService).clear(customerId);
        }

        private ResultActions performPlaceOrderRequest() throws Exception {
            var json = objectMapper.writeValueAsString(checkoutRequestDto);

            return mockMvc.perform(post("/api/cart/checkout/confirm/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json)
            );
        }
    }
}
