package com.example.eshop.cart.application.usecases.cartitem;

import com.example.eshop.cart.domain.Cart;
import com.example.eshop.cart.domain.CartRepository;
import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import com.example.eshop.customer.infrastructure.auth.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithUserDetails;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest
class CartItemServiceImplIntegrationTest {
    private final static String CUSTOMER_ID = "1";
    private final static String CUSTOMER_EMAIL = "test@test.test";

    @MockBean
    private CartRepository cartRepository;

    @Autowired
    private CartItemService cartItemService;

    @TestConfiguration
    public static class Config {
        @Bean
        @Primary
        public UserDetailsService userDetailsService() {
            return username -> new UserDetailsImpl(CUSTOMER_EMAIL, "pass", CUSTOMER_ID);
        }
    }

    @BeforeEach
    void setUp() {
        var cart = new Cart(CUSTOMER_ID);
        when(cartRepository.findByNaturalId(eq(CUSTOMER_ID))).thenReturn(Optional.of(cart));
    }

    @Test
    @WithUserDetails(CUSTOMER_EMAIL)
    void whenUpsertCalledByNonCartOwner_thenThrowAccessDeniedException() {
        var command = new UpsertCartItemCommand("2", Ean.fromString("0799439112766"), 10);

        assertThatThrownBy(() -> cartItemService.upsert(command))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @WithUserDetails(CUSTOMER_EMAIL)
    void whenUpsertCalledByCartOwner_thenNoExceptionIsThrown() {
        var command = new UpsertCartItemCommand(CUSTOMER_ID, Ean.fromString("0799439112766"), 10);

        assertThatNoException().isThrownBy(() -> cartItemService.upsert(command));
    }
}
