package com.example.eshop.cart.application.services.clearcart;

import com.example.eshop.cart.FakeData;
import com.example.eshop.cart.application.services.cartquery.CartQueryService;
import com.example.eshop.cart.domain.Cart;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ClearCartServiceImplTest {
    private final String customerId = FakeData.customerId();
    private final Cart cart = FakeData.cart(customerId);

    private ClearCartService clearCartService;

    @BeforeEach
    void setUp() {
        var cartQueryService = mock(CartQueryService.class);
        when(cartQueryService.getForCustomerOrCreate(customerId)).thenReturn(cart);

        clearCartService = new ClearCartServiceImpl(cartQueryService);
    }

    @Test
    void whenClearCart_thenCartHasNoItems() {
        // When
        clearCartService.clear(customerId);

        // Then
        assertThat(cart.getItems()).isEmpty();
    }
}
