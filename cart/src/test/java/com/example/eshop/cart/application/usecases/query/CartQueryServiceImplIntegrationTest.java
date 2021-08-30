package com.example.eshop.cart.application.usecases.query;

import com.example.eshop.cart.config.AuthConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ContextConfiguration;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ContextConfiguration(classes = AuthConfig.class)
class CartQueryServiceImplIntegrationTest {
    @Autowired
    CartQueryService cartQueryService;

    @Test
    @WithUserDetails(AuthConfig.CUSTOMER_EMAIL)
    void whenGetForCustomerCalledByNonOwner_thenThrowAccessDeniedException() {
        assertThatThrownBy(() -> cartQueryService.getForCustomer("non-owner"))
                .isInstanceOf(AccessDeniedException.class);
    }
}
