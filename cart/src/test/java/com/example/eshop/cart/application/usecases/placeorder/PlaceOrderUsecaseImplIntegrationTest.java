package com.example.eshop.cart.application.usecases.placeorder;

import com.example.eshop.cart.ExcludeKafkaConfig;
import com.example.eshop.cart.config.AuthConfig;
import com.example.eshop.cart.domain.checkout.order.CreateOrderDto;
import com.example.eshop.sharedtest.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithUserDetails;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ExcludeKafkaConfig
@IntegrationTest
class PlaceOrderUsecaseImplIntegrationTest {
    @Autowired
    private PlaceOrderUsecase placeOrderUsecase;

    @Test
    @WithUserDetails(AuthConfig.CUSTOMER_EMAIL)
    void givenNonAuthorizedUser_whenPlaceOrder_thenThrowAccessDeniedException() {
        var createOrderDto = CreateOrderDto.builder()
                .customerId("nonAuthorizedCustomerId")
                .build();

        assertThatThrownBy(() -> placeOrderUsecase.place(createOrderDto))
                .isInstanceOf(AccessDeniedException.class);
    }
}
