package com.example.eshop.cart.rest.controllers;

import com.example.eshop.auth.WithMockCustomJwtAuthentication;
import com.example.eshop.cart.FakeData;
import com.example.eshop.cart.application.usecases.cartitemcrud.AddCartItemCommand;
import com.example.eshop.cart.application.usecases.cartitemcrud.CartItemCrudService;
import com.example.eshop.cart.application.usecases.cartitemcrud.NotEnoughQuantityException;
import com.example.eshop.cart.application.usecases.cartitemcrud.RemoveCartItemCommand;
import com.example.eshop.cart.application.usecases.cartquery.CartQueryService;
import com.example.eshop.cart.client.api.model.CartDto;
import com.example.eshop.cart.testconfig.ControllerTest;
import com.example.eshop.cart.domain.Cart;
import com.example.eshop.cart.domain.CartItemNotFoundException;
import com.example.eshop.cart.testconfig.AuthConfig;
import com.example.eshop.cart.rest.mappers.CartMapper;
import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import com.example.eshop.sharedkernel.domain.valueobject.Money;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CartController.class)
@ControllerTest
class CartControllerTest {
    @MockBean
    private CartQueryService cartQueryService;
    @MockBean
    private CartItemCrudService cartItemCrudService;
    @MockBean
    private CartMapper cartMapper;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private final String customerId = AuthConfig.CUSTOMER_ID;
    private final Cart cart = new Cart(customerId);
    private final CartDto cartDto = new CartDto();
    private final Ean ean = FakeData.ean();
    private final Money price = Money.USD(12.34);
    private final int quantity = 7;

    @BeforeEach
    void setUp() {
        cart.addItem(ean, price, quantity);

        when(cartQueryService.getForCustomerOrCreate(customerId)).thenReturn(cart);

        when(cartMapper.toCartDto(cart)).thenReturn(cartDto);
    }

    @Nested
    class GetCartTest {
        @Test
        @WithMockCustomJwtAuthentication(customerId = AuthConfig.CUSTOMER_ID)
        void whenGetCart_thenReturnCartForTheAuthenticatedCustomer() throws Exception {
            var expectedJson = objectMapper.writeValueAsString(cartDto);

            performGetCartRequest()
                    .andExpect(status().isOk())
                    .andExpect(content().json(expectedJson));

            verify(cartQueryService).getForCustomerOrCreate(AuthConfig.CUSTOMER_ID);
            verify(cartMapper).toCartDto(cart);
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
        private final AddCartItemCommand addCartItemCommand = new AddCartItemCommand(AuthConfig.CUSTOMER_ID, ean, quantity);

        @Test
        @WithMockCustomJwtAuthentication(customerId = AuthConfig.CUSTOMER_ID)
        void whenPutCartItem_thenCartItemServiceUpsertIsCalledAndCartIsReturned() throws Exception {
            var expectedJson = objectMapper.writeValueAsString(cartDto);

            performPutCartItemRequest()
                    .andExpect(status().isOk())
                    .andExpect(content().json(expectedJson));

            verify(cartItemCrudService).add(addCartItemCommand);
            verify(cartMapper).toCartDto(cart);
        }

        @Test
        void givenUnauthorizedRequest_whenPutCartItem_thenReturn401() throws Exception {
            performPutCartItemRequest()
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockCustomJwtAuthentication(customerId = AuthConfig.CUSTOMER_ID)
        void whenPutItemWithExceededQuantity_thenReturn400() throws Exception {
            doThrow(new NotEnoughQuantityException("", 0, quantity)).when(cartItemCrudService).add(addCartItemCommand);

            performPutCartItemRequest()
                    .andExpect(status().isBadRequest());

            verify(cartItemCrudService).add(addCartItemCommand);
        }

        private ResultActions performPutCartItemRequest() throws Exception {
            var json = """
                    {
                        "ean": "%s",
                        "quantity": %d
                    }
                    """.formatted(ean, quantity);

            return mockMvc.perform(put("/api/cart/items")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json));
        }
    }

    @Nested
    class RemoveCartItemTest {
        @Test
        @WithMockCustomJwtAuthentication(customerId = AuthConfig.CUSTOMER_ID)
        void whenRemoveCartItem_thenCartItemServiceRemoveIsCalledAndCartIsReturned() throws Exception {
            var expectedJson = objectMapper.writeValueAsString(cartDto);
            var expectedCommand = new RemoveCartItemCommand(AuthConfig.CUSTOMER_ID, ean);

            performRemoveCartItemRequest()
                    .andExpect(status().isOk())
                    .andExpect(content().json(expectedJson));

            verify(cartItemCrudService).remove(expectedCommand);
            verify(cartMapper).toCartDto(cart);
        }

        @Test
        @WithMockCustomJwtAuthentication(customerId = AuthConfig.CUSTOMER_ID)
        void givenNonExistingEan_whenRemoveCartItem_thenReturn404() throws Exception {
            var expectedCommand = new RemoveCartItemCommand(AuthConfig.CUSTOMER_ID, ean);

            doThrow(CartItemNotFoundException.class).when(cartItemCrudService).remove(expectedCommand);

            performRemoveCartItemRequest()
                    .andExpect(status().isNotFound());

            verify(cartItemCrudService).remove(expectedCommand);
        }

        @Test
        void givenUnauthorizedRequest_whenRemoveCartItem_thenReturn401() throws Exception {
            performRemoveCartItemRequest()
                    .andExpect(status().isUnauthorized());
        }

        private ResultActions performRemoveCartItemRequest() throws Exception {
            return mockMvc.perform(delete("/api/cart/items/" + ean));
        }
    }
}
