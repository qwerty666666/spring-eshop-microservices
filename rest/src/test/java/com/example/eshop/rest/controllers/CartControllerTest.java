package com.example.eshop.rest.controllers;

import com.example.eshop.cart.application.usecases.cart.cartitem.CartItemService;
import com.example.eshop.cart.application.usecases.cart.cartitem.RemoveCartItemCommand;
import com.example.eshop.cart.application.usecases.cart.cartitem.UpsertCartItemCommand;
import com.example.eshop.cart.application.usecases.cart.query.CartQueryService;
import com.example.eshop.cart.application.usecases.cart.query.dto.CartDto;
import com.example.eshop.cart.application.usecases.cart.query.dto.CartItemDto;
import com.example.eshop.cart.domain.cart.CartItemNotFoundException;
import com.example.eshop.rest.config.AuthConfig;
import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import com.example.eshop.sharedkernel.domain.valueobject.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
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
class CartControllerTest {
    @TestConfiguration
    @Import(AuthConfig.class)
    @ComponentScan("com.example.eshop.rest.mappers")
    public static class Config {
    }

    @MockBean
    private CartQueryService cartQueryService;

    @MockBean
    private CartItemService cartItemService;

    @Autowired
    private MockMvc mockMvc;

    private final static String CART_ID = "123";
    private final static Ean EAN = Ean.fromString("5901234123457");
    private final static Double PRICE = 12.34;
    private final static String CURRENCY = "USD";
    private final static int QUANTITY = 7;
    private final static String PRODUCT_NAME = "Test Product";
    private final static String EXPECTED_CART_JSON = """
            {
                 "id": "123",
                 "items": [
                     {
                         "ean": "5901234123457",
                         "price": {
                             "amount": 12.34,
                             "currency": "USD"
                         },
                         "productName": "Test Product",
                         "quantity": 7
                        }
                 ]
            }
            """;

    @BeforeEach
    void setUp() {
        var cart = new CartDto(CART_ID, List.of(new CartItemDto(EAN, Money.of(PRICE, CURRENCY), QUANTITY, PRODUCT_NAME)));
        when(cartQueryService.getForCustomer(AuthConfig.CUSTOMER_ID)).thenReturn(cart);
    }

    @Nested
    @ContextConfiguration(classes = Config.class)
    class GetCart {
        @Test
        @WithUserDetails(AuthConfig.CUSTOMER_EMAIL)
        void whenGetCart_thenReturnCartForTheAuthenticatedCustomer() throws Exception {
            performGetCartRequest()
                    .andExpect(status().isOk())
                    .andExpect(content().json(EXPECTED_CART_JSON));

            verify(cartQueryService).getForCustomer(AuthConfig.CUSTOMER_ID);
        }

        @Test
        void givenAnauthorizedRequest_whenGetCart_thenReturn401() throws Exception {
            performGetCartRequest()
                    .andExpect(status().isUnauthorized());
        }

        private ResultActions performGetCartRequest() throws Exception {
            return mockMvc.perform(get("/api/cart"));
        }
    }

    @Nested
    @ContextConfiguration(classes = Config.class)
    class PutCartItem {
        @Test
        @WithUserDetails(AuthConfig.CUSTOMER_EMAIL)
        void whenPutCartItem_thenCartItemServiceUpsertIsCalledAndCartIsReturned() throws Exception {
            performPutCartItemRequest()
                    .andExpect(status().isOk())
                    .andExpect(content().json(EXPECTED_CART_JSON));

            verify(cartItemService).upsert(eq(new UpsertCartItemCommand(AuthConfig.CUSTOMER_ID, EAN, QUANTITY)));
        }

        @Test
        void givenAnauthorizedRequest_whenPutCartItem_thenReturn401() throws Exception {
            performPutCartItemRequest()
                    .andExpect(status().isUnauthorized());
        }

        private ResultActions performPutCartItemRequest() throws Exception {
            return mockMvc.perform(put("/api/cart/items")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {
                            "ean": "%s",
                            "quantity": %d
                        }
                        """.formatted(EAN, QUANTITY)
                    )
            );
        }
    }

    @Nested
    @ContextConfiguration(classes = Config.class)
    class RemoveCartItem {
        @Test
        @WithUserDetails(AuthConfig.CUSTOMER_EMAIL)
        void whenRemoveCartItem_thenCartItemServiceRemoveIsCalledAndCartIsReturned() throws Exception {
            performRemoveCartItemRequest()
                    .andExpect(status().isOk())
                    .andExpect(content().json(EXPECTED_CART_JSON));

            verify(cartItemService).remove(eq(new RemoveCartItemCommand(AuthConfig.CUSTOMER_ID, EAN)));
        }

        @Test
        @WithUserDetails(AuthConfig.CUSTOMER_EMAIL)
        void givenNonExistingEan_whenRemoveCartItem_thenReturn404() throws Exception {
            doThrow(CartItemNotFoundException.class).when(cartItemService).remove(any());

            performRemoveCartItemRequest()
                    .andExpect(status().isNotFound());

            verify(cartItemService).remove(eq(new RemoveCartItemCommand(AuthConfig.CUSTOMER_ID, EAN)));
        }

        @Test
        void givenAnauthorizedRequest_whenRemoveCartItem_thenReturn401() throws Exception {
            performRemoveCartItemRequest()
                    .andExpect(status().isUnauthorized());
        }

        private ResultActions performRemoveCartItemRequest() throws Exception {
            return mockMvc.perform(delete("/api/cart/items/" + EAN));
        }
    }
}