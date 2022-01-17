package com.example.eshop.rest.mappers;

import com.example.eshop.cart.domain.cart.Cart;
import com.example.eshop.cart.domain.cart.CartItem;
import com.example.eshop.catalog.application.product.ProductCrudService;
import com.example.eshop.catalog.domain.product.Product;
import com.example.eshop.catalog.domain.product.Sku;
import com.example.eshop.rest.dto.CartDto;
import com.example.eshop.rest.dto.CartItemDto;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;

@Mapper(
        componentModel = "spring",
        uses = { EanMapper.class, ImageMapper.class, AttributeMapper.class },
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public abstract class CartMapper {
    private ProductCrudService productCrudService;

    // we can't use constructor injection in MapStruct for not @Mapper::uses dependencies
    @Autowired
    public void setProductCrudService(ProductCrudService productCrudService) {
        this.productCrudService = productCrudService;
    }

    @Nullable
    public CartDto toCartDto(@Nullable Cart cart) {
        if (cart == null) {
            return null;
        }

        var ean = cart.getItems().stream().map(CartItem::getEan).toList();
        var productInfo = productCrudService.getByEan(ean);

        var dto = new CartDto();
        dto.setId(cart.getId() == null ? null : cart.getId().toString());

        var items = cart.getItems().stream()
                .map(item -> {
                    var product = productInfo.get(item.getEan());
                    var sku = product == null ? null : product.getSku(item.getEan());

                    return toCartItemDto(item, product, sku);
                })
                .toList();

        dto.setItems(items);

        return dto;
    }

    @Mapping(target = "images", source = "product.images", conditionExpression = "java(product != null)")
    @Mapping(target = "productName", source = "product.name", conditionExpression = "java(product != null)")
    @Mapping(target = "attributes", source = "sku.attributes", conditionExpression = "java(sku != null)")
    @Mapping(target = "availableQuantity", source = "sku.availableQuantity", conditionExpression = "java(sku != null)")
    @Mapping(target = "ean", source = "cartItem.ean")
    @Mapping(target = "price", source = "cartItem.itemPrice")
    public abstract CartItemDto toCartItemDto(CartItem cartItem, @Nullable Product product, @Nullable Sku sku);
}
