package com.example.eshop.checkout.rest.controllers;

import com.example.eshop.auth.WithMockCustomJwtAuthentication;
import com.example.eshop.cart.client.CartServiceClient;
import com.example.eshop.cart.client.model.CartDto;
import com.example.eshop.checkout.application.services.CreateOrderDto;
import com.example.eshop.checkout.application.services.checkoutprocess.CheckoutProcessService;
import com.example.eshop.checkout.application.services.placeorder.PlaceOrderService;
import com.example.eshop.checkout.client.model.CheckoutFormDto;
import com.example.eshop.checkout.client.model.CheckoutRequestDto;
import com.example.eshop.checkout.config.AuthConfig;
import com.example.eshop.checkout.config.ControllerTest;
import com.example.eshop.checkout.domain.order.DeliveryAddress;
import com.example.eshop.checkout.FakeData;
import com.example.eshop.checkout.rest.mappers.CheckoutMapper;
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
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CheckoutController.class)
@ControllerTest
class CheckoutControllerTest {
    @MockBean
    private CartServiceClient cartServiceClient;
    @MockBean
    private PlaceOrderService placeOrderService;
    @MockBean
    private CheckoutProcessService checkoutProcessService;
    @MockBean
    private CheckoutMapper checkoutMapper;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private final String customerId = AuthConfig.CUSTOMER_ID;
    private final CartDto cartDto = FakeData.cartDto();
    private final CreateOrderDto createOrderDto = FakeData.createOrderDto(customerId);
    private final CheckoutRequestDto checkoutRequestDto = new CheckoutRequestDto()
            .address(FakeData.deliveryAddressDto())
            .deliveryServiceId(null)
            .paymentServiceId(null);

    @BeforeEach
    void setUp() {
        when(cartServiceClient.getCart(customerId))
                .thenReturn(Mono.just(cartDto));
        when(cartServiceClient.clear(customerId))
                .thenReturn(Mono.just(cartDto));
        when(checkoutMapper.toCreateOrderDto(checkoutRequestDto, customerId, cartDto))
                .thenReturn(createOrderDto);
    }

    @Nested
    class CheckoutProcessTest {
        @Test
        void givenUnauthorizedRequest_whenCheckout_thenReturn401() throws Exception {
            performCheckoutRequest()
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockCustomJwtAuthentication(customerId = AuthConfig.CUSTOMER_ID)
        void givenInvalidRequest_whenCheckout_thenReturn400() throws Exception {
            var errors = new Errors().addError("field", "cart.null");

            when(checkoutProcessService.process(createOrderDto))
                    .thenThrow(new ValidationException(errors));

            performCheckoutRequest()
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors[?(@.field == 'field')]").exists());
        }

        @Test
        @WithMockCustomJwtAuthentication(customerId = AuthConfig.CUSTOMER_ID)
        void whenCheckoutRequest_thenReturnCheckout() throws Exception {
            // Given
            var checkoutForm = FakeData.checkoutForm();
            var checkoutFormDto = new CheckoutFormDto();

            when(checkoutMapper.toCheckoutFormDto(checkoutForm))
                    .thenReturn(checkoutFormDto);
            when(checkoutProcessService.process(createOrderDto))
                    .thenReturn(checkoutForm);

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

            return mockMvc.perform(post("/api/checkout/")
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
            // Given
            var errors = new Errors().addError("field", "cart.null");

            when(placeOrderService.place(createOrderDto))
                    .thenThrow(new ValidationException(errors));

            // When + Then
            performPlaceOrderRequest()
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors[?(@.field == 'field')]").exists());
        }

        @Test
        @WithMockCustomJwtAuthentication(customerId = AuthConfig.CUSTOMER_ID)
        void whenPlaceOrder_thenCartIsClearedAndReturn200() throws Exception {
            // Given
            when(placeOrderService.place(createOrderDto))
                    .thenReturn(FakeData.order());

            // When + Then
            performPlaceOrderRequest()
                    .andExpect(status().isCreated())
                    .andExpect(header().exists(HttpHeaders.LOCATION));

            verify(placeOrderService).place(createOrderDto);
            verify(cartServiceClient).clear(customerId);
        }

        private ResultActions performPlaceOrderRequest() throws Exception {
            var json = objectMapper.writeValueAsString(checkoutRequestDto);

            return mockMvc.perform(post("/api/checkout/confirm/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json)
            );
        }
    }
}
