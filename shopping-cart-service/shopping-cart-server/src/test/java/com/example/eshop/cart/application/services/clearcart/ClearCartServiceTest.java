package com.example.eshop.cart.application.services.clearcart;

import com.example.eshop.auth.WithMockCustomJwtAuthentication;
import com.example.eshop.cart.FakeData;
import com.example.eshop.cart.application.services.cartquery.CartQueryService;
import com.example.eshop.cart.config.AuthConfig;
import com.example.eshop.cart.domain.Cart;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
@WithMockCustomJwtAuthentication(customerId = AuthConfig.CUSTOMER_ID)
class ClearCartServiceTest {
    @MockBean
    private CartQueryService cartQueryService;

    @Autowired
    private ClearCartService clearCartService;

    private final String customerId = AuthConfig.CUSTOMER_ID;
    private final String notAuthorizedCustomerId = "non-owner";
    private final Cart cart = FakeData.cart(customerId);

    @BeforeEach
    void setUp() {
        when(cartQueryService.getForCustomerOrCreate(customerId)).thenReturn(cart);
    }

    @Test
    void whenClearCalledByNonOwner_thenThrowAccessDeniedException() {
        assertThatExceptionOfType(AccessDeniedException.class)
                .isThrownBy(() -> clearCartService.clear(notAuthorizedCustomerId));
    }

    @Test
    void whenClearCart_thenCartHasNoItems() {
        // When
        clearCartService.clear(customerId);

        // Then
        assertThat(cart.getItems()).isEmpty();
    }
}
