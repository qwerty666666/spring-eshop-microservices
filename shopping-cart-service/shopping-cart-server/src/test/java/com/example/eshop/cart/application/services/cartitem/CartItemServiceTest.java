package com.example.eshop.cart.application.services.cartitem;

import com.example.eshop.auth.WithMockCustomJwtAuthentication;
import com.example.eshop.cart.FakeData;
import com.example.eshop.cart.application.services.cartquery.CartQueryService;
import com.example.eshop.cart.config.AuthConfig;
import com.example.eshop.cart.domain.Cart;
import com.example.eshop.cart.domain.CartItem;
import com.example.eshop.catalog.client.CatalogServiceClient;
import com.example.eshop.catalog.client.model.SkuWithProductDto;
import com.example.eshop.catalog.client.model.ProductDto;
import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import com.example.eshop.sharedkernel.domain.valueobject.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
@WithMockCustomJwtAuthentication(customerId = AuthConfig.CUSTOMER_ID)
class CartItemServiceTest {
    private final String customerId = AuthConfig.CUSTOMER_ID;
    private final String nonAuthorizedCustomerId = "non-authorized";
    private final Cart cart = FakeData.cart(customerId);
    private final CartItem existedInCartCartItem = cart.getItems().get(0);
    private final Ean existedInCartEan = existedInCartCartItem.getEan();
    private final Ean newEan = Ean.fromString("7239429347811");
    private final Ean nonExistedInCatalogEan = Ean.fromString("1237569802342");
    private final int availableQuantity = 10;

    @MockBean
    private CartQueryService cartQueryService;
    @MockBean
    private CatalogServiceClient catalogServiceClient;

    @Autowired
    private CartItemService cartItemService;

    @BeforeEach
    void setUp() {
        // CartQueryService

        when(cartQueryService.getForCustomerOrCreate(customerId)).thenReturn(cart);

        // CatalogService

        var existedInCartSku = SkuWithProductDto.builder()
                .ean(existedInCartCartItem.getEan())
                .quantity(existedInCartCartItem.getQuantity())
                .product(ProductDto.builder()
                        .name("Test Product")
                        .build()
                )
                .build();

        var newSku = SkuWithProductDto.builder()
                .ean(newEan)
                .price(Money.USD(10))
                .quantity(availableQuantity)
                .product(ProductDto.builder()
                        .name("Test Product 2")
                        .build()
                )
                .build();

        when(catalogServiceClient.getSku(newEan)).thenReturn(Mono.just(newSku));
        when(catalogServiceClient.getSku(existedInCartEan)).thenReturn(Mono.just(existedInCartSku));
        when(catalogServiceClient.getSku(nonExistedInCatalogEan)).thenReturn(Mono.empty());
    }

    @Nested
    class AddTests {
        @Test
        void whenAddCalledByNonCartOwner_thenThrowAccessDeniedException() {
            var command = new AddCartItemCommand(nonAuthorizedCustomerId, newEan, 1);

            assertThatThrownBy(() -> cartItemService.add(command))
                    .isInstanceOf(AccessDeniedException.class);
        }

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

    @Nested
    class RemoveTests {
        @Test
        void whenRemoveCalledByNonCartOwner_thenThrowAccessDeniedException() {
            var command = new RemoveCartItemCommand(nonAuthorizedCustomerId, existedInCartEan);

            assertThatThrownBy(() -> cartItemService.remove(command))
                    .isInstanceOf(AccessDeniedException.class);
        }
    }
}
