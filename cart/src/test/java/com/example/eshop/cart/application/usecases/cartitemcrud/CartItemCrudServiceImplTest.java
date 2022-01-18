package com.example.eshop.cart.application.usecases.cartitemcrud;

import com.example.eshop.cart.domain.cart.Cart;
import com.example.eshop.cart.domain.cart.CartRepository;
import com.example.eshop.cart.infrastructure.tests.FakeData;
import com.example.eshop.catalog.application.services.productcrudservice.ProductCrudService;
import com.example.eshop.catalog.application.services.productcrudservice.ProductNotFoundException;
import com.example.eshop.catalog.domain.product.Product;
import com.example.eshop.catalog.domain.product.Sku;
import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import com.example.eshop.sharedkernel.domain.valueobject.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CartItemCrudServiceImplTest {
    private final String customerId = FakeData.customerId();
    private final Ean newEan = Ean.fromString("7239429347811");
    private final Ean nonExistedInCatalogEan = Ean.fromString("1237569802342");
    private final Cart cart = FakeData.cart(customerId);
    private final Ean existedInCartEan = cart.getItems().get(0).getEan();

    private CartItemCrudService cartItemCrudService;

    @BeforeEach
    void setUp() {
        // CartRepository

        var cartRepository = mock(CartRepository.class);
        when(cartRepository.findByNaturalId(customerId)).thenReturn(Optional.of(cart));

        // ProductCrudService

        var product = Product.builder()
                .name("Test Product")
                .addSku(Sku.builder()
                        .ean(existedInCartEan)
                        .price(Money.USD(10))
                        .availableQuantity(10)
                        .build()
                )
                .addSku(Sku.builder()
                        .ean(newEan)
                        .price(Money.USD(10))
                        .availableQuantity(10)
                        .build()
                )
                .build();

        var productCrudService = mock(ProductCrudService.class);
        when(productCrudService.getByEan(newEan)).thenReturn(product);
        when(productCrudService.getByEan(nonExistedInCatalogEan)).thenThrow(new ProductNotFoundException(null, ""));

        // CartItemService

        cartItemCrudService = new CartItemCrudServiceImpl(cartRepository, productCrudService);
    }

    @Test
    void givenNonExistingInCartEan_whenAdd_thenNewCartItemShouldBeCreatedAndSavedToCart() {
        // Given
        int quantity = 1;

        // When
        cartItemCrudService.add(new AddCartItemCommand(customerId, newEan, quantity));

        // Then
        assertThat(cart.containsItem(newEan)).isTrue();
        assertThat(cart.getItem(newEan).getQuantity()).isEqualTo(quantity);
    }

    @Test
    void givenExistingInCartEan_whenAdd_thenCartItemQuantityShouldBeChanged() {
        // Given
        int quantity = 1;

        // When
        cartItemCrudService.add(new AddCartItemCommand(customerId, existedInCartEan, quantity));

        // Then
        assertThat(cart.containsItem(existedInCartEan)).isTrue();
        assertThat(cart.getItem(existedInCartEan).getQuantity()).isEqualTo(quantity);
    }

    @Test
    void givenNonExistedInCatalogEan_whenAdd_thenThrowProductNotFoundException() {
        var command = new AddCartItemCommand(customerId, nonExistedInCatalogEan, 1);

        assertThatExceptionOfType(ProductNotFoundException.class)
                .isThrownBy(() -> cartItemCrudService.add(command));
    }
}
