package com.example.eshop.cart.application.services.cartquery;

import com.example.eshop.cart.FakeData;
import com.example.eshop.cart.application.services.createcart.CreateCartService;
import com.example.eshop.cart.domain.CartRepository;
import org.junit.jupiter.api.Test;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CartQueryServiceImplTest {
    @Test
    void whenCustomerHasNoCart_thenNewCartIsCreated() {
        // Given
        var customerId = "customerId";
        var expectedCart = FakeData.emptyCart(customerId);

        var cartRepository = mock(CartRepository.class);
        when(cartRepository.findByNaturalId(customerId)).thenReturn(Optional.empty());

        var createCartService = mock(CreateCartService.class);
        when(createCartService.create(customerId)).thenReturn(expectedCart);

        var cartQueryService = new CartQueryServiceImpl(cartRepository, createCartService);

        // When
        var cart = cartQueryService.getForCustomerOrCreate(customerId);

        // Then
        assertThat(cart).isEqualTo(expectedCart);
        verify(createCartService).create(customerId);
    }
}
