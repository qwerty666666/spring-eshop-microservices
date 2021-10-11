package com.example.eshop.cart.application.usecases.createcart;

import com.example.eshop.cart.domain.cart.CartRepository;
import com.example.eshop.cart.utils.FakeData;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class CreateCartServiceImplTest {
    @Test
    void whenCreateCart_thenCartIsSavedToRepository() {
        // Given
        var customerId = FakeData.customerId();
        var expectedCart = FakeData.emptyCart(customerId);

        var cartRepository = mock(CartRepository.class);

        var createCartService = new CreateCartServiceImpl(cartRepository);

        // When
        createCartService.create(customerId);

        // Then
        verify(cartRepository).save(eq(expectedCart));
    }
}
