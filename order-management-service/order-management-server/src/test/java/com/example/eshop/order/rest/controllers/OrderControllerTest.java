package com.example.eshop.order.rest.controllers;

import com.example.eshop.auth.WithMockCustomJwtAuthentication;
import com.example.eshop.order.application.services.queryorder.OrderNotFoundException;
import com.example.eshop.order.application.services.queryorder.QueryOrderService;
import com.example.eshop.order.config.AuthConfig;
import com.example.eshop.order.config.ControllerTest;
import com.example.eshop.order.config.MapperTestsConfig;
import com.example.eshop.order.domain.order.Order;
import com.example.eshop.order.FakeData;
import com.example.eshop.order.rest.mappers.OrderMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
@ControllerTest
@Import(MapperTestsConfig.class)
class OrderControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private OrderMapper orderMapper;

    @MockBean
    private QueryOrderService queryOrderService;

    private Order order;
    private Page<Order> orders;

    // authenticated user is not owner of this order
    private Order nonOwnerOrder;
    private UUID notExistedOrderId = UUID.fromString("bc046a6e-1e2b-468a-9a98-1ce99f087249");

    @BeforeEach
    void setUp() {
        order = FakeData.order(AuthConfig.CUSTOMER_ID);
        orders = new PageImpl<>(List.of(order));

        nonOwnerOrder = FakeData.order(UUID.fromString("c8ca0699-1a8b-423a-bf62-12f21eb58a57"), "non-owner-customer-id");

        when(queryOrderService.getForCustomer(eq(AuthConfig.CUSTOMER_ID), any())).thenReturn(orders);
        when(queryOrderService.getById(order.getId())).thenReturn(order);
        when(queryOrderService.getById(nonOwnerOrder.getId())).thenReturn(nonOwnerOrder);
        when(queryOrderService.getById(notExistedOrderId)).thenThrow(OrderNotFoundException.class);
    }

    @Nested
    class GetOrderListTests {
        private final int pageSize = 30;
        private final int page = 1;

        @Test
        @WithMockCustomJwtAuthentication(customerId = AuthConfig.CUSTOMER_ID)
        void whenGetOrderList_thenReturnOrdersForTheAuthenticatedCustomer() throws Exception {
            var expectedJson = objectMapper.writeValueAsString(orderMapper.toPagedOrderListDto(orders));
            var expectedPageable = PageRequest.ofSize(pageSize)
                    .withPage(page - 1)
                    .withSort(Direction.DESC, "creationDate");

            performGetOrderListRequest()
                    .andExpect(status().isOk())
                    .andExpect(content().json(expectedJson));

            verify(queryOrderService).getForCustomer(AuthConfig.CUSTOMER_ID, expectedPageable);
        }

        @Test
        void givenUnauthorizedRequest_whenGetOrderList_thenReturn401() throws Exception {
            performGetOrderListRequest()
                    .andExpect(status().isUnauthorized());
        }

        private ResultActions performGetOrderListRequest() throws Exception {
            return mockMvc.perform(
                    get("/api/orders")
                            .param("page", String.valueOf(page))
                            .param("per_page", String.valueOf(pageSize))
            );
        }
    }

    @Nested
    class GetOrderTests {
        @Test
        @WithMockCustomJwtAuthentication(customerId = AuthConfig.CUSTOMER_ID)
        void whenGetOrder_thenReturnOrderWithGivenId() throws Exception {
            var expectedJson = objectMapper.writeValueAsString(orderMapper.toOrderDto(order));

            performGetOrderRequest(order.getId())
                    .andExpect(status().isOk())
                    .andExpect(content().json(expectedJson));

            verify(queryOrderService).getById(order.getId());
        }

        @Test
        void givenUnauthorizedRequest_whenGetOrder_thenReturn401() throws Exception {
            performGetOrderRequest(order.getId())
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockCustomJwtAuthentication(customerId = AuthConfig.CUSTOMER_ID)
        void givenNonOwnerOrderId_whenGetOrder_thenReturn403() throws Exception {
            performGetOrderRequest(nonOwnerOrder.getId())
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockCustomJwtAuthentication(customerId = AuthConfig.CUSTOMER_ID)
        void givenNonExistedOrderId_whenGetOrder_thenReturn404() throws Exception {
            performGetOrderRequest(notExistedOrderId)
                    .andExpect(status().isNotFound());
        }

        private ResultActions performGetOrderRequest(UUID id) throws Exception {
            return mockMvc.perform(get("/api/orders/" + id));
        }
    }
}
