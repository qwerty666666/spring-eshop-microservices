package com.example.eshop.cart.application.services.cartitem;

import com.example.eshop.cart.FakeData;
import com.example.eshop.cart.application.services.cartquery.CartQueryService;
import com.example.eshop.cart.domain.Cart;
import com.example.eshop.cart.domain.CartItem;
import com.example.eshop.catalog.client.CatalogService;
import com.example.eshop.catalog.client.SkuWithProductDto;
import com.example.eshop.catalog.client.api.model.MoneyDto;
import com.example.eshop.catalog.client.api.model.ProductDto;
import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CartItemServiceImplTest {
    private final String customerId = FakeData.customerId();
    private final Cart cart = FakeData.cart(customerId);
    private final CartItem existedInCartCartItem = cart.getItems().get(0);
    private final Ean existedInCartEan = existedInCartCartItem.getEan();
    private final Ean newEan = Ean.fromString("7239429347811");
    private final Ean nonExistedInCatalogEan = Ean.fromString("1237569802342");
    private final int availableQuantity = 10;

    private CartItemService cartItemService;

    @BeforeEach
    void setUp() {
        // CartQueryService

        var cartQueryService = mock(CartQueryService.class);
        when(cartQueryService.getForCustomerOrCreate(customerId)).thenReturn(cart);

        // CatalogService

        var existedInCartSku = SkuWithProductDto.builder()
                .ean(existedInCartCartItem.getEan().toString())
                .quantity(existedInCartCartItem.getQuantity())
                .product(ProductDto.builder()
                        .name("Test Product")
                        .build()
                )
                .build();

        var newSku = SkuWithProductDto.builder()
                .ean(newEan.toString())
                .price(new MoneyDto(BigDecimal.valueOf(10), "USD"))
                .quantity(availableQuantity)
                .product(ProductDto.builder()
                        .name("Test Product")
                        .build()
                )
                .build();

        var catalogService = mock(CatalogService.class);
        when(catalogService.getSku(newEan)).thenReturn(Mono.just(newSku));
        when(catalogService.getSku(existedInCartEan)).thenReturn(Mono.just(existedInCartSku));
        when(catalogService.getSku(nonExistedInCatalogEan)).thenReturn(Mono.empty());

        // CartItemService

        cartItemService = new CartItemServiceImpl(catalogService, cartQueryService);
    }

    @Nested
    class AddTest {
        @Test
        void givenNonExistingInCartEan_whenAdd_thenNewCartItemShouldBeCreatedAndSavedToCart() {
            // Given
            int quantity = 1;

            // When
            cartItemService.add(new AddCartItemCommand(customerId, newEan, quantity));

            // Then
            assertThat(cart.containsItem(newEan)).isTrue();
            assertThat(cart.getItem(newEan).getQuantity()).isEqualTo(quantity);
        }

        @Test
        void givenExistingInCartEan_whenAdd_thenCartItemQuantityShouldBeChanged() {
            // Given
            int quantity = 1;

            // When
            cartItemService.add(new AddCartItemCommand(customerId, existedInCartEan, quantity));

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
            var exception = catchThrowableOfType(() -> cartItemService.add(addCommand),
                    AddToCartRuleViolationException.class);

            // Then
            assertThat(exception.getCause()).isInstanceOf(NotEnoughQuantityException.class);

            var cause = (NotEnoughQuantityException) exception.getCause();
            assertThat(cause.getAvailableQuantity()).isEqualTo(availableQuantity);
            assertThat(cause.getRequiredQuantity()).isEqualTo(quantity);

            assertThat(cart.containsItem(newEan)).isFalse();
        }

        @Test
        void givenNonExistedInCatalogEan_whenAdd_thenThrowProductNotFoundException() {
            var command = new AddCartItemCommand(customerId, nonExistedInCatalogEan, 1);

            assertThatExceptionOfType(ProductNotFoundException.class)
                    .isThrownBy(() -> cartItemService.add(command));
        }
    }
}
