package com.example.eshop.cart.application.usecases.cartquery;

import com.example.eshop.auth.WithMockCustomJwtAuthentication;
import com.example.eshop.cart.testconfig.ExcludeKafkaConfig;
import com.example.eshop.cart.testconfig.AuthConfig;
import com.example.eshop.sharedtest.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@IntegrationTest
@AutoConfigureTestDatabase
@ExcludeKafkaConfig
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
