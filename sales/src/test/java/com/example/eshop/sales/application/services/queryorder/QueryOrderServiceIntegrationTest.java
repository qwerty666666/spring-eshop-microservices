package com.example.eshop.sales.application.services.queryorder;

import com.example.eshop.sales.config.AuthConfig;
import com.example.eshop.sales.domain.Order;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.spring.api.DBRider;
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
    @Autowired
    private QueryOrderService queryOrderService;

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
                .containsOnly(UUID.fromString("11111111-1111-1111-1111-111111111111"));
    }
}
