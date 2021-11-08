package com.example.eshop.cart.application.usecases.clearcart;

import com.example.eshop.cart.config.AuthConfig;
import com.example.eshop.cart.domain.cart.Cart;
import com.example.eshop.cart.domain.cart.CartRepository;
import com.example.eshop.cart.infrastructure.tests.FakeData;
import com.example.eshop.sharedtest.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithUserDetails;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.when;

@SpringBootTest
@IntegrationTest
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
    @WithUserDetails(AuthConfig.CUSTOMER_EMAIL)
    void whenClearCalledByNonOwner_thenThrowAccessDeniedException() {
        assertThatExceptionOfType(AccessDeniedException.class)
                .isThrownBy(() -> clearCartService.clear(NON_OWNER_CUSTOMER_ID));
    }
}
