package com.example.eshop.cart.application.usecases.cartitem;

import com.example.eshop.cart.config.AuthConfig;
import com.example.eshop.cart.domain.Cart;
import com.example.eshop.cart.domain.CartRepository;
import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ContextConfiguration;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest
@ContextConfiguration(classes = AuthConfig.class)
class CartItemServiceImplIntegrationTest {
    @MockBean
    private CartRepository cartRepository;

    @Autowired
    private CartItemService cartItemService;

    @Autowired
    UserDetailsService userDetailsService;

    @BeforeEach
    void setUp() {
        var cart = new Cart(AuthConfig.CUSTOMER_ID);
        when(cartRepository.findByNaturalId(eq(AuthConfig.CUSTOMER_ID))).thenReturn(Optional.of(cart));
    }

    @Test
    @WithUserDetails(AuthConfig.CUSTOMER_EMAIL)
    void whenUpsertCalledByNonCartOwner_thenThrowAccessDeniedException() {
        var command = new UpsertCartItemCommand("2", Ean.fromString("0799439112766"), 10);

        assertThatThrownBy(() -> cartItemService.upsert(command))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @WithUserDetails(AuthConfig.CUSTOMER_EMAIL)
    void whenUpsertCalledByCartOwner_thenNoExceptionIsThrown() {
        var command = new UpsertCartItemCommand(AuthConfig.CUSTOMER_ID, Ean.fromString("0799439112766"), 10);

        assertThatNoException().isThrownBy(() -> cartItemService.upsert(command));
    }
}
