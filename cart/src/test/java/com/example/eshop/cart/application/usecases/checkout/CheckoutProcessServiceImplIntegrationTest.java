package com.example.eshop.cart.application.usecases.checkout;

import com.example.eshop.auth.WithMockCustomJwtAuthentication;
import com.example.eshop.cart.ExcludeKafkaConfig;
import com.example.eshop.cart.config.AuthConfig;
import com.example.eshop.cart.domain.checkout.order.CreateOrderDto;
import com.example.eshop.sharedtest.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ExcludeKafkaConfig
@IntegrationTest
class CheckoutProcessServiceImplIntegrationTest {
    @Autowired
    CheckoutProcessService checkoutProcessService;

    @Test
    @WithMockCustomJwtAuthentication(customerId = AuthConfig.CUSTOMER_ID)
    void givenNonAuthorizedUser_whenProcess_thenThrowAccessDeniedException() {
        var createOrderDto = CreateOrderDto.builder()
                .customerId("nonAuthorizedCustomerId")
                .build();

        assertThatThrownBy(() -> checkoutProcessService.process(createOrderDto))
                .isInstanceOf(AccessDeniedException.class);
    }
}
