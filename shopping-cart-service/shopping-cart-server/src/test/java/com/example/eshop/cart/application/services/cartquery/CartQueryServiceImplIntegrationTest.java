package com.example.eshop.cart.application.services.cartquery;

import com.example.eshop.auth.WithMockCustomJwtAuthentication;
import com.example.eshop.cart.config.AuthConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@AutoConfigureTestDatabase
@ActiveProfiles("test")
class CartQueryServiceImplIntegrationTest {
    @Autowired
    private CartQueryService cartQueryService;

    @Test
    @WithMockCustomJwtAuthentication(customerId = AuthConfig.CUSTOMER_ID)
    void whenGetForCustomerCalledByNonOwner_thenThrowAccessDeniedException() {
        assertThatThrownBy(() -> cartQueryService.getForCustomerOrCreate("non-owner"))
                .isInstanceOf(AccessDeniedException.class);
    }
}
