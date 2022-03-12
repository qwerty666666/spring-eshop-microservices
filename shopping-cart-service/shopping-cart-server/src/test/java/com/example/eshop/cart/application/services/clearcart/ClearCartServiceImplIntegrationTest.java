package com.example.eshop.cart.application.services.clearcart;

import com.example.eshop.auth.WithMockCustomJwtAuthentication;
import com.example.eshop.cart.FakeData;
import com.example.eshop.cart.config.AuthConfig;
import com.example.eshop.cart.domain.Cart;
import com.example.eshop.cart.domain.CartRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ActiveProfiles;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
class ClearCartServiceImplIntegrationTest {
    @Autowired
    private ClearCartService clearCartService;

    @MockBean
    private CartRepository cartRepository;

    private Cart cart;
    private static final String OWNER_CUSTOMER_ID = AuthConfig.CUSTOMER_ID;
    private static final String NON_OWNER_CUSTOMER_ID = "non-owner";

    @BeforeEach
    void setUp() {
        cart = FakeData.cart(OWNER_CUSTOMER_ID);

        when(cartRepository.findByNaturalId(OWNER_CUSTOMER_ID)).thenReturn(Optional.of(cart));
    }

    @Test
    @WithMockCustomJwtAuthentication(customerId = AuthConfig.CUSTOMER_ID)
    void whenClearCalledByNonOwner_thenThrowAccessDeniedException() {
        assertThatExceptionOfType(AccessDeniedException.class)
                .isThrownBy(() -> clearCartService.clear(NON_OWNER_CUSTOMER_ID));
    }
}
