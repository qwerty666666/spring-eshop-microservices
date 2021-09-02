package com.example.eshop.cart.application.usecases.cart.cartitem;

import com.example.eshop.cart.domain.cart.Cart;
import com.example.eshop.cart.domain.cart.CartRepository;
import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class CartItemServiceImplTest {
    @Test
    void givenNonExistingInCartEan_whenUpsert_thenNewCartItemShouldBeCreatedAndSavedToCart() {
        // Given
        String customerId = "1";
        Ean ean = Ean.fromString("0799439112766");
        var qty = 10;

        var cart = mock(Cart.class);

        var cartRepository = mock(CartRepository.class);
        when(cartRepository.findByNaturalId(eq(customerId))).thenReturn(Optional.of(cart));

        CartItemService service = new CartItemServiceImpl(cartRepository);

        // When
        service.upsert(new UpsertCartItemCommand(customerId, ean, qty));

        // Then
        verify(cart).addItem(eq(ean), eq(qty));
    }

    @Test
    void givenExistingInCartEan_whenUpsert_thenCartItemQuantityShouldBeChanged() {
        // Given
        String customerId = "1";
        Ean ean = Ean.fromString("0799439112766");
        var qty = 10;

        var cart = mock(Cart.class);
        when(cart.containsItem(eq(ean))).thenReturn(true);

        var cartRepository = mock(CartRepository.class);
        when(cartRepository.findByNaturalId(eq(customerId))).thenReturn(Optional.of(cart));

        CartItemService service = new CartItemServiceImpl(cartRepository);

        // When
        service.upsert(new UpsertCartItemCommand(customerId, ean, qty));

        // Then
        verify(cart).changeItemQuantity(eq(ean), eq(qty));
    }
}
