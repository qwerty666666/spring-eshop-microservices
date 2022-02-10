package com.example.eshop.cart.application.usecases.cartitemcrud;

import com.example.eshop.auth.WithMockCustomJwtAuthentication;
import com.example.eshop.cart.ExcludeKafkaConfig;
import com.example.eshop.cart.config.AuthConfig;
import com.example.eshop.cart.domain.cart.Cart;
import com.example.eshop.cart.domain.cart.CartRepository;
import com.example.eshop.cart.infrastructure.tests.FakeData;
import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import com.example.eshop.sharedtest.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.access.AccessDeniedException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExcludeKafkaConfig
@IntegrationTest
@WithMockCustomJwtAuthentication(customerId = AuthConfig.CUSTOMER_ID)
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
        when(cartRepository.findByNaturalId(OWNER_CUSTOMER_ID)).thenReturn(Optional.of(cart));
    }

    @Test
    void whenAddCalledByNonCartOwner_thenThrowAccessDeniedException() {
        var command = new AddCartItemCommand(NON_OWNER_CUSTOMER_ID, FakeData.ean(), 10);

        assertThatThrownBy(() -> cartItemCrudService.add(command))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void whenRemoveCalledByNonCartOwner_thenThrowAccessDeniedException() {
        var command = new RemoveCartItemCommand(NON_OWNER_CUSTOMER_ID, existedInCartEan);

        assertThatThrownBy(() -> cartItemCrudService.remove(command))
                .isInstanceOf(AccessDeniedException.class);
    }
}
