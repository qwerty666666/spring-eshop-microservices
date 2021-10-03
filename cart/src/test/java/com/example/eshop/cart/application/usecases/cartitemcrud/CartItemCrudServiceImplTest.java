package com.example.eshop.cart.application.usecases.cartitemcrud;

import com.example.eshop.cart.domain.cart.Cart;
import com.example.eshop.cart.domain.cart.CartRepository;
import com.example.eshop.catalog.application.product.ProductCrudService;
import com.example.eshop.catalog.domain.product.Product;
import com.example.eshop.catalog.domain.product.Sku;
import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import com.example.eshop.sharedkernel.domain.valueobject.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CartItemCrudServiceImplTest {
    private final String customerId = "1";
    private final Ean ean = Ean.fromString("0799439112766");
    private final Money price = Money.USD(10);
    private final int qty = 10;
    private final String productName = "Test Product";

    private Cart cart;
    private Product product;

    private CartRepository cartRepository;
    private ProductCrudService productCrudService;

    @BeforeEach
    void setUp() {
        cart = mock(Cart.class);

        cartRepository = mock(CartRepository.class);
        when(cartRepository.findByNaturalId(eq(customerId))).thenReturn(Optional.of(cart));

        product = Product.builder()
                .name(productName)
                .build();
        product.addSku(Sku.builder()
                .ean(ean)
                .price(price)
                .availableQuantity(10)
                .build()
        );

        productCrudService = mock(ProductCrudService.class);
        when(productCrudService.getByEan(eq(ean))).thenReturn(product);
    }

    @Test
    void givenNonExistingInCartEan_whenUpsert_thenNewCartItemShouldBeCreatedAndSavedToCart() {
        // Given
        CartItemCrudService service = new CartItemCrudServiceImpl(cartRepository, productCrudService);

        // When
        service.upsert(new UpsertCartItemCommand(customerId, ean, qty));

        // Then
        verify(cart).addItem(eq(ean), eq(price), eq(qty), eq(productName));
    }

    @Test
    void givenExistingInCartEan_whenUpsert_thenCartItemQuantityShouldBeChanged() {
        // Given
        when(cart.containsItem(eq(ean))).thenReturn(true);

        CartItemCrudService service = new CartItemCrudServiceImpl(cartRepository, productCrudService);

        // When
        service.upsert(new UpsertCartItemCommand(customerId, ean, qty));

        // Then
        verify(cart).changeItemQuantity(eq(ean), eq(qty));
    }
}
