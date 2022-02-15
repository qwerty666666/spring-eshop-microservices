package com.example.eshop.order.application.services.queryorder;

import com.example.eshop.auth.WithMockCustomJwtAuthentication;
import com.example.eshop.order.config.AuthConfig;
import com.example.eshop.order.domain.order.Order;
import com.example.eshop.sharedtest.dbtests.DbTest;
import com.github.database.rider.core.api.dataset.DataSet;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DbTest
@SpringBootTest
class QueryOrderServiceIntegrationTest {
    private final static UUID ORDER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private final static UUID NOT_EXISTED_ORDER_ID = UUID.fromString("c8ca0699-1a8b-423a-bf62-12f21eb58a57");

    @Autowired
    private QueryOrderService queryOrderService;

    @Nested
    @WithMockCustomJwtAuthentication(customerId = AuthConfig.CUSTOMER_ID)
    class GetForCustomerTests {
        @Test
        void whenGetForCustomerCalledByNonOwner_thenThrowAccessDeniedException() {
            var pageable = Pageable.unpaged();

            assertThatThrownBy(() -> queryOrderService.getForCustomer("non-owner", pageable))
                    .isInstanceOf(AccessDeniedException.class);
        }

        @Test
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
