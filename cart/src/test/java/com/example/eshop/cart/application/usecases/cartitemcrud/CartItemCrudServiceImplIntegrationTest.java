package com.example.eshop.cart.application.usecases.cartitemcrud;

import com.example.eshop.cart.config.AuthConfig;
import com.example.eshop.cart.domain.cart.Cart;
import com.example.eshop.cart.domain.cart.CartRepository;
import com.example.eshop.cart.infrastructure.tests.FakeData;
import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithUserDetails;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest
class CartItemCrudServiceImplIntegrationTest {
    private final static String OWNER_CUSTOMER_ID = AuthConfig.CUSTOMER_ID;
    private final static String NON_OWNER_CUSTOMER_ID = "non-owner";
    private final Cart cart = FakeData.cart(OWNER_CUSTOMER_ID);
    private final Ean existedInCartEan = cart.getItems().get(0).getEan();

    @MockBean
    private CartRepository cartRepository;

    @Autowired
    private CartItemCrudService cartItemCrudService;

    @BeforeEach
    void setUp() {
        when(cartRepository.findByNaturalId(eq(OWNER_CUSTOMER_ID))).thenReturn(Optional.of(cart));
    }

    @Test
    @WithUserDetails(AuthConfig.CUSTOMER_EMAIL)
    void whenAddCalledByNonCartOwner_thenThrowAccessDeniedException() {
        var command = new AddCartItemCommand(NON_OWNER_CUSTOMER_ID, FakeData.ean(), 10);

        assertThatThrownBy(() -> cartItemCrudService.add(command))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @WithUserDetails(AuthConfig.CUSTOMER_EMAIL)
    void whenAddCalledByCartOwner_thenNoExceptionIsThrown() {
        var command = new AddCartItemCommand(OWNER_CUSTOMER_ID, FakeData.ean(), 10);

        assertThatNoException().isThrownBy(() -> cartItemCrudService.add(command));
    }

    @Test
    @WithUserDetails(AuthConfig.CUSTOMER_EMAIL)
    void whenRemoveCalledByNonCartOwner_thenThrowAccessDeniedException() {
        var command = new RemoveCartItemCommand(NON_OWNER_CUSTOMER_ID, existedInCartEan);

        assertThatThrownBy(() -> cartItemCrudService.remove(command))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @WithUserDetails(AuthConfig.CUSTOMER_EMAIL)
    void whenRemoveCalledByCartOwner_thenNoExceptionIsThrown() {
        var command = new RemoveCartItemCommand(OWNER_CUSTOMER_ID, existedInCartEan);

        assertThatNoException().isThrownBy(() -> cartItemCrudService.remove(command));
    }
}
