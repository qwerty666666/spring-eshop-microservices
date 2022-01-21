package com.example.eshop.cart.application.usecases.cartitemcrud;

import com.example.eshop.cart.application.services.cataloggateway.CatalogGateway;
import com.example.eshop.cart.application.services.cataloggateway.ProductNotFoundException;
import com.example.eshop.cart.domain.cart.Cart;
import com.example.eshop.cart.domain.cart.CartItem;
import com.example.eshop.cart.domain.cart.CartRepository;
import com.example.eshop.cart.infrastructure.tests.FakeData;
import com.example.eshop.catalog.client.api.model.Money;
import com.example.eshop.catalog.client.api.model.Product;
import com.example.eshop.catalog.client.api.model.Sku;
import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CartItemCrudServiceImplTest {
    private final String customerId = FakeData.customerId();
    private final Cart cart = FakeData.cart(customerId);
    private final CartItem existedInCartCartItem = cart.getItems().get(0);
    private final Ean existedInCartEan = existedInCartCartItem.getEan();
    private final Ean newEan = Ean.fromString("7239429347811");
    private final Ean nonExistedInCatalogEan = Ean.fromString("1237569802342");
    private final int availableQuantity = 10;

    private CartItemCrudService cartItemCrudService;

    @BeforeEach
    void setUp() {
        // CartRepository

        var cartRepository = mock(CartRepository.class);
        when(cartRepository.findByNaturalId(customerId)).thenReturn(Optional.of(cart));

        // CatalogGateway

        var existedInCartProduct = Product.builder()
                .name("test")
                .sku(List.of(
                        Sku.builder()
                                .ean(existedInCartCartItem.getEan().toString())
                                .quantity(existedInCartCartItem.getQuantity())
                                .build()
                ))
                .build();
        var product = Product.builder()
                .name("Test Product")
                .sku(List.of(
                        Sku.builder()
                                .ean(existedInCartEan.toString())
                                .price(new Money(BigDecimal.valueOf(10), "USD"))
                                .quantity(availableQuantity)
                                .build(),
                        Sku.builder()
                                .ean(newEan.toString())
                                .price(new Money(BigDecimal.valueOf(10), "USD"))
                                .quantity(availableQuantity)
                                .build()
                ))
                .build();

        var catalogGateway = mock(CatalogGateway.class);
        when(catalogGateway.getProductByEan(newEan)).thenReturn(product);
        when(catalogGateway.getProductByEan(existedInCartEan)).thenReturn(existedInCartProduct);
        when(catalogGateway.getProductByEan(nonExistedInCatalogEan)).thenThrow(new ProductNotFoundException(""));

        // CartItemService

        cartItemCrudService = new CartItemCrudServiceImpl(cartRepository, catalogGateway);
    }

    @Nested
    class AddTest {
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
        void givenExceededQuantity_whenAddNewItem_thenNotEnoughQuantityIsThrownAndCartItemNotAdded() {
            // Given
            int quantity = availableQuantity + 1;
            var addCommand = new AddCartItemCommand(customerId, newEan, quantity);

            // When
            var exception = catchThrowableOfType(() -> cartItemCrudService.add(addCommand),
                    NotEnoughQuantityException.class);

            // Then
            assertThat(exception.getAvailableQuantity()).isEqualTo(availableQuantity);
            assertThat(exception.getRequiredQuantity()).isEqualTo(quantity);

            assertThat(cart.containsItem(newEan)).isFalse();
        }

        @Test
        void givenNonExistedInCatalogEan_whenAdd_thenThrowProductNotFoundException() {
            var command = new AddCartItemCommand(customerId, nonExistedInCatalogEan, 1);

            assertThatExceptionOfType(ProductNotFoundException.class)
                    .isThrownBy(() -> cartItemCrudService.add(command));
        }
    }
}
