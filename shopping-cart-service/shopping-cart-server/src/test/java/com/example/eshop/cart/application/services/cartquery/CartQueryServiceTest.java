package com.example.eshop.cart.application.services.cartquery;

import com.example.eshop.auth.WithMockCustomJwtAuthentication;
import com.example.eshop.cart.FakeData;
import com.example.eshop.cart.application.services.createcart.CreateCartService;
import com.example.eshop.cart.config.AuthConfig;
import com.example.eshop.cart.domain.CartRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ActiveProfiles;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
@WithMockCustomJwtAuthentication(customerId = AuthConfig.CUSTOMER_ID)
class CartQueryServiceTest {
    private final String customerId = AuthConfig.CUSTOMER_ID;
    
    @MockBean
    private CartRepository cartRepository;
    @MockBean
    private CreateCartService createCartService;

    @Autowired
    private CartQueryService cartQueryService;

    @Test
    void whenGetForCustomerCalledByNonOwner_thenThrowAccessDeniedException() {
        assertThatThrownBy(() -> cartQueryService.getForCustomerOrCreate("non-owner"))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void whenCustomerHasNoCart_thenNewCartIsCreated() {
        // Given
        var expectedCart = FakeData.emptyCart(customerId);

        when(cartRepository.findByNaturalId(customerId)).thenReturn(Optional.empty());
        when(createCartService.create(customerId)).thenReturn(expectedCart);

        // When
        var cart = cartQueryService.getForCustomerOrCreate(customerId);

        // Then
        assertThat(cart).isEqualTo(expectedCart);
        verify(createCartService).create(customerId);
    }
}
