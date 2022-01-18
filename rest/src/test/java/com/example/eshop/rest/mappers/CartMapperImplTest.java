package com.example.eshop.rest.mappers;

import com.example.eshop.cart.domain.cart.Cart;
import com.example.eshop.catalog.application.services.productcrudservice.ProductCrudService;
import com.example.eshop.catalog.domain.file.File;
import com.example.eshop.catalog.domain.product.Attribute;
import com.example.eshop.catalog.domain.product.AttributeValue;
import com.example.eshop.catalog.domain.product.Product;
import com.example.eshop.catalog.domain.product.Sku;
import com.example.eshop.rest.config.MappersConfig;
import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import com.example.eshop.sharedkernel.domain.valueobject.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = MappersConfig.class)
class CartMapperImplTest {
    @MockBean
    private ProductCrudService productCrudService;

    @Autowired
    private CartMapper mapper;

    private Cart cart;
    private Map<Ean, Product> productInfo;

    @BeforeEach
    void setUp() {
        var attribute = new Attribute(1L, "size");

        var sku1 = Sku.builder()
                .ean(Ean.fromString("1111111111111"))
                .price(Money.USD(10))
                .availableQuantity(10)
                .addAttribute(new AttributeValue(attribute, "XL", 1))
                .build();
        var sku2 = Sku.builder()
                .ean(Ean.fromString("2222222222222"))
                .price(Money.USD(20))
                .availableQuantity(20)
                .addAttribute(new AttributeValue(attribute, "XXL", 2))
                .build();

        var product = Product.builder()
                .name("product")
                .addImage(new File("file-1"))
                .addImage(new File("file-2"))
                .addSku(sku1)
                .addSku(sku2)
                .build();

        productInfo = Map.of(
                sku1.getEan(), product,
                sku2.getEan(), product
        );

        when(productCrudService.getByEan(anyList())).thenReturn(productInfo);

        cart = new Cart("1");
        cart.addItem(sku1.getEan(), sku1.getPrice(), sku1.getAvailableQuantity());
        cart.addItem(sku2.getEan(), sku2.getPrice(), sku2.getAvailableQuantity());
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    void testToCartDto() {
        // When
        var dto = mapper.toCartDto(cart);

        // Then
        Assertions.assertCartEquals(cart, productInfo, dto);
    }
}
