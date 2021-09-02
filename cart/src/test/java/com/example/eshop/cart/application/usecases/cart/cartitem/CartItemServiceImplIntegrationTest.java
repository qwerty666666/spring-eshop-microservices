package com.example.eshop.cart.application.usecases.cart.cartitem;

import com.example.eshop.cart.config.AuthConfig;
import com.example.eshop.cart.domain.cart.Cart;
import com.example.eshop.cart.domain.cart.CartRepository;
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
    private final static String NON_OWNER_CUSTOMER_ID = "2";
    private final static Ean EAN = Ean.fromString("0799439112766");

    @MockBean
    private CartRepository cartRepository;

    @Autowired
    private CartItemService cartItemService;

    @Autowired
    UserDetailsService userDetailsService;

    @BeforeEach
    void setUp() {
        var cart = new Cart(AuthConfig.CUSTOMER_ID);
        cart.addItem(EAN, 10);
        when(cartRepository.findByNaturalId(eq(AuthConfig.CUSTOMER_ID))).thenReturn(Optional.of(cart));
    }

    @Test
    @WithUserDetails(AuthConfig.CUSTOMER_EMAIL)
    void whenUpsertCalledByNonCartOwner_thenThrowAccessDeniedException() {
        var command = new UpsertCartItemCommand(NON_OWNER_CUSTOMER_ID, EAN, 10);

        assertThatThrownBy(() -> cartItemService.upsert(command))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @WithUserDetails(AuthConfig.CUSTOMER_EMAIL)
    void whenUpsertCalledByCartOwner_thenNoExceptionIsThrown() {
        var command = new UpsertCartItemCommand(AuthConfig.CUSTOMER_ID, EAN, 10);

        assertThatNoException().isThrownBy(() -> cartItemService.upsert(command));
    }

    @Test
    @WithUserDetails(AuthConfig.CUSTOMER_EMAIL)
    void whenRemoveCalledByNonCartOwner_thenThrowAccessDeniedException() {
        var command = new RemoveCartItemCommand(NON_OWNER_CUSTOMER_ID, EAN);

        assertThatThrownBy(() -> cartItemService.remove(command))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @WithUserDetails(AuthConfig.CUSTOMER_EMAIL)
    void whenRemoveCalledByCartOwner_thenNoExceptionIsThrown() {
        var command = new RemoveCartItemCommand(AuthConfig.CUSTOMER_ID, EAN);

        assertThatNoException().isThrownBy(() -> cartItemService.remove(command));
    }
}
