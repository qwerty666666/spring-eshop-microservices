package com.example.eshop.rest.controllers;

import com.example.eshop.rest.config.AuthConfig;
import com.example.eshop.rest.config.ControllerTestConfig;
import com.example.eshop.rest.mappers.OrderMapper;
import com.example.eshop.sales.application.services.queryorder.QueryOrderService;
import com.example.eshop.sales.domain.Order;
import com.example.eshop.sales.infrastructure.tests.FakeData;
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
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
@ActiveProfiles("test")
@Import(ControllerTestConfig.class)
class OrderControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private OrderMapper orderMapper;

    @MockBean
    private QueryOrderService queryOrderService;

    private Page<Order> orders;

    @BeforeEach
    void setUp() {
        orders = new PageImpl<>(List.of(FakeData.order(AuthConfig.CUSTOMER_ID)));

        when(queryOrderService.getForCustomer(eq(AuthConfig.CUSTOMER_ID), any())).thenReturn(orders);
    }

    @Nested
    class GetOrderListTests {
        private final int pageSize = 30;
        private final int page = 1;

        @Test
        @WithUserDetails(AuthConfig.CUSTOMER_EMAIL)
        void whenGetOrderList_thenReturnOrdersForTheAuthenticatedCustomer() throws Exception {
            var expectedJson = objectMapper.writeValueAsString(orderMapper.toPagedOrderListDto(orders));
            var expectedPageable = PageRequest.ofSize(pageSize)
                    .withPage(page - 1)
                    .withSort(Direction.DESC, "creationDate");

            performGetOrderListRequest()
                    .andExpect(status().isOk())
                    .andExpect(content().json(expectedJson));

            verify(queryOrderService).getForCustomer(eq(AuthConfig.CUSTOMER_ID), eq(expectedPageable));
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
}
