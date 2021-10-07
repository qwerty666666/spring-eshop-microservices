package com.example.eshop.rest.controllers;

import com.example.eshop.cart.application.usecases.cartitemcrud.AddCartItemCommand;
import com.example.eshop.cart.application.usecases.cartitemcrud.CartItemCrudService;
import com.example.eshop.cart.application.usecases.cartitemcrud.RemoveCartItemCommand;
import com.example.eshop.cart.application.usecases.cartquery.CartQueryService;
import com.example.eshop.cart.domain.cart.Cart;
import com.example.eshop.cart.domain.cart.CartItemNotFoundException;
import com.example.eshop.rest.config.AuthConfig;
import com.example.eshop.rest.config.ControllerTestConfig;
import com.example.eshop.rest.mappers.CartMapper;
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

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CartController.class)
@ActiveProfiles("test")
@Import(ControllerTestConfig.class)
class CartControllerTest {
    private final static Ean EAN = Ean.fromString("5901234123457");
    private final static Money PRICE = Money.USD(12.34);
    private final static int QUANTITY = 7;
    private final static String PRODUCT_NAME = "Test Product";

    @MockBean
    private CartQueryService cartQueryService;
    @MockBean
    private CartItemCrudService cartItemCrudService;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private CartMapper cartMapper;

    private Cart cart;

    @BeforeEach
    void setUp() {
        cart = new Cart(AuthConfig.CUSTOMER_ID);
        cart.addItem(EAN, PRICE, QUANTITY, PRODUCT_NAME);

        when(cartQueryService.getForCustomer(AuthConfig.CUSTOMER_ID)).thenReturn(cart);
    }

    @Nested
    class GetCart {
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
    class PutItem {
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
    class RemoveCartItem {
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
}