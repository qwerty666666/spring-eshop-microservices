package com.example.eshop.cart.application.usecases.createcart;

import com.example.eshop.cart.FakeData;
import com.example.eshop.cart.domain.CartRepository;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
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
        var newCart = createCartService.create(customerId);

        // Then
        assertThat(newCart).isEqualTo(expectedCart);
        verify(cartRepository).save(expectedCart);
    }
}
