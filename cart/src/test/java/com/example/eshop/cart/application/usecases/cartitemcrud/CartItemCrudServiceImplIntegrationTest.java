package com.example.eshop.cart.application.usecases.cartitemcrud;

import com.example.eshop.cart.config.AuthConfig;
import com.example.eshop.cart.domain.cart.Cart;
import com.example.eshop.cart.domain.cart.CartRepository;
import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import com.example.eshop.sharedkernel.domain.valueobject.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ContextConfiguration;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest
@ContextConfiguration(classes = AuthConfig.class)
class CartItemCrudServiceImplIntegrationTest {
    private final static String NON_OWNER_CUSTOMER_ID = "2";
    private final static Ean EAN = Ean.fromString("0799439112766");

    @MockBean
    private CartRepository cartRepository;

    @Autowired
    private CartItemCrudService cartItemCrudService;

    @BeforeEach
    void setUp() {
        var cart = new Cart(AuthConfig.CUSTOMER_ID);
        cart.addItem(EAN, Money.USD(10), 10, "Test Product");
        when(cartRepository.findByNaturalId(eq(AuthConfig.CUSTOMER_ID))).thenReturn(Optional.of(cart));
    }

    @Test
    @WithUserDetails(AuthConfig.CUSTOMER_EMAIL)
    void whenUpsertCalledByNonCartOwner_thenThrowAccessDeniedException() {
        var command = new AddCartItemCommand(NON_OWNER_CUSTOMER_ID, EAN, 10);

        assertThatThrownBy(() -> cartItemCrudService.add(command))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @WithUserDetails(AuthConfig.CUSTOMER_EMAIL)
    void whenUpsertCalledByCartOwner_thenNoExceptionIsThrown() {
        var command = new AddCartItemCommand(AuthConfig.CUSTOMER_ID, EAN, 10);

        assertThatNoException().isThrownBy(() -> cartItemCrudService.add(command));
    }

    @Test
    @WithUserDetails(AuthConfig.CUSTOMER_EMAIL)
    void whenRemoveCalledByNonCartOwner_thenThrowAccessDeniedException() {
        var command = new RemoveCartItemCommand(NON_OWNER_CUSTOMER_ID, EAN);

        assertThatThrownBy(() -> cartItemCrudService.remove(command))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @WithUserDetails(AuthConfig.CUSTOMER_EMAIL)
    void whenRemoveCalledByCartOwner_thenNoExceptionIsThrown() {
        var command = new RemoveCartItemCommand(AuthConfig.CUSTOMER_ID, EAN);

        assertThatNoException().isThrownBy(() -> cartItemCrudService.remove(command));
    }
}
