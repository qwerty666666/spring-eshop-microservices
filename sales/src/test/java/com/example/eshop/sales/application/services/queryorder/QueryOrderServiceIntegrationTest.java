package com.example.eshop.sales.application.services.queryorder;

import com.example.eshop.sales.config.AuthConfig;
import com.example.eshop.sales.domain.Order;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.spring.api.DBRider;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithUserDetails;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@DBRider
class QueryOrderServiceIntegrationTest {
    private final static UUID ORDER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private final static UUID NOT_EXISTED_ORDER_ID = UUID.fromString("c8ca0699-1a8b-423a-bf62-12f21eb58a57");

    @Autowired
    private QueryOrderService queryOrderService;

    @Nested
    class GetForCustomerTests {
        @Test
        @WithUserDetails(AuthConfig.CUSTOMER_EMAIL)
        void whenGetForCustomerCalledByNonOwner_thenThrowAccessDeniedException() {
            assertThatThrownBy(() -> queryOrderService.getForCustomer("non-owner", Pageable.unpaged()))
                    .isInstanceOf(AccessDeniedException.class);
        }

        @Test
        @WithUserDetails(AuthConfig.CUSTOMER_EMAIL)
        @DataSet("orders.yml")
        void whenGetForCustomer_thenReturnOrderOnlyForGivenCustomer() {
            var orders = queryOrderService.getForCustomer(AuthConfig.CUSTOMER_ID, Pageable.ofSize(100));

            assertThat(orders.getTotalElements()).isEqualTo(1);
            assertThat(orders.getContent())
                    .extracting(Order::getId)
                    .containsOnly(ORDER_ID);
        }
    }

    @Nested
    class GetByIdTests {
        @Test
        @DataSet("orders.yml")
        void whenGetById_thenReturnOrderWithGivenId() {
            var order = queryOrderService.getById(ORDER_ID);

            assertThat(order.getId()).isEqualTo(ORDER_ID);
        }

        @Test
        @DataSet("orders.yml")
        void givenNonExistedOrderId_whenGetById_thenThrowsOrderNotFoundException() {
            assertThatThrownBy(() -> queryOrderService.getById(NOT_EXISTED_ORDER_ID))
                    .isInstanceOf(OrderNotFoundException.class);
        }
    }
}
