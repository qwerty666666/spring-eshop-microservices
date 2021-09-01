package com.example.eshop.cart.application.usecases.cart.create;

import com.example.eshop.cart.application.usecases.cart.create.CreateCartService;
import com.example.eshop.cart.application.usecases.cart.create.CreateCartServiceImpl;
import com.example.eshop.cart.domain.Cart;
import com.example.eshop.cart.domain.CartRepository;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class CreateCartServiceImplTest {
    @Test
    void whenCreateCart_thenCartIsSavedToRepository() {
        // given
        var customerId = "1";
        var expectedCart = new Cart(customerId);

        var cartRepository = mock(CartRepository.class);
        CreateCartService createCartService = new CreateCartServiceImpl(cartRepository);

        // when
        createCartService.create(customerId);

        // then
        verify(cartRepository).save(eq(expectedCart));
    }
}
