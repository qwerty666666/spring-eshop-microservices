package com.example.eshop.cart.application.usecases.placeorder;

import com.example.eshop.cart.application.usecases.checkout.OrderDto;
import com.example.eshop.cart.config.AuthConfig;
import com.example.eshop.cart.domain.checkout.order.DeliveryAddress;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithUserDetails;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class PlaceOrderServiceImplIntegrationTest {
    @Autowired
    PlaceOrderService placeOrderService;

    @Test
    @WithUserDetails(AuthConfig.CUSTOMER_EMAIL)
    void whenPlaceOrderWithNotAuthorizedCustomerId_thenThrowAccessDeniedException() {
        var orderDto = new OrderDto("nonAuthorizedCustomerId", new DeliveryAddress(), null, null);

        assertThatThrownBy(() -> placeOrderService.place(orderDto))
                .isInstanceOf(AccessDeniedException.class);
    }
}
