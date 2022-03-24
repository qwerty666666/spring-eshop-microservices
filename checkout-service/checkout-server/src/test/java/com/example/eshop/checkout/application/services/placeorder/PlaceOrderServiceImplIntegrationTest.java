package com.example.eshop.checkout.application.services.placeorder;

import com.example.eshop.auth.WithMockCustomJwtAuthentication;
import com.example.eshop.checkout.FakeData;
import com.example.eshop.checkout.config.AuthConfig;
import com.example.eshop.checkout.application.services.CreateOrderDto;
import com.example.eshop.sharedtest.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@IntegrationTest
class PlaceOrderServiceImplIntegrationTest {
    @Autowired
    private PlaceOrderService placeOrderService;

    @Test
    @WithMockCustomJwtAuthentication(customerId = AuthConfig.CUSTOMER_ID)
    void givenNonAuthorizedUser_whenPlaceOrder_thenThrowAccessDeniedException() {
        var createOrderDto = FakeData.createOrderDto("nonAuthorizedCustomerId");

        assertThatThrownBy(() -> placeOrderService.place(createOrderDto))
                .isInstanceOf(AccessDeniedException.class);
    }
}
