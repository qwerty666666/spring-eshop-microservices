package com.example.eshop.rest.mappers;

import com.example.eshop.cart.domain.cart.Cart;
import com.example.eshop.cart.domain.cart.CartItem;
import com.example.eshop.catalog.client.CatalogService;
import com.example.eshop.catalog.client.SkuWithProductDto;
import com.example.eshop.rest.dto.CartDto;
import com.example.eshop.rest.dto.CartItemDto;
import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@Mapper(
        componentModel = "spring",
        uses = { RestEanMapper.class },
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public abstract class CartMapper {
    private CatalogService catalogService;

    // we can't use constructor injection in MapStruct for not @Mapper::uses dependencies
    @Autowired
    public void setCatalogService(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    public CartDto toCartDto(Cart cart) {
        var ean = cart.getItems().stream().map(CartItem::getEan).toList();

        Map<Ean, SkuWithProductDto> sku;

        if (ean.isEmpty()) {
            sku = Collections.emptyMap();
        } else {
            sku = catalogService.getSku(ean)
                    .blockOptional()
                    // empty result is impossible there (otherwise it will be contract violation,
                    // and we end up with NPE, that is OK in this case I think), but we handle
                    // null value to pass static analysis. And it's fucking weird ¯\_(ツ)_/¯
                    .orElseThrow(() -> new RuntimeException("getSku() return null"));
        }

        return new CartDto()
                .id(cart.getId() == null ? null : cart.getId().toString())
                .items(cart.getItems().stream().map(item -> toCartItemDto(item, sku)).toList());
    }

    protected CartItemDto toCartItemDto(CartItem item, Map<Ean, SkuWithProductDto> skuMap) {
        // TODO handle missing product case.
        // It is possible that we remove product from catalog, but it is still in the Cart
        // Therefore, we should handle this instead of throwing exceptions.
        // We can either:
        //  1. sync cart and catalog
        //  2. or notify client that this CartItem is unavailable anymore

        var sku = Optional.ofNullable(skuMap.get(item.getEan()))
                .orElseThrow(() -> new RuntimeException("Sku for EAN " + item.getEan() + " does not exist"));

        return toCartItemDto(item, sku);
    }

    @Mapping(target = "images", source = "sku.product.images")
    @Mapping(target = "productName", source = "sku.product.name")
    @Mapping(target = "attributes", source = "sku.attributes")
    @Mapping(target = "availableQuantity", source = "sku.quantity")
    @Mapping(target = "quantity", source = "cartItem.quantity")
    @Mapping(target = "ean", source = "cartItem.ean")
    @Mapping(target = "price", source = "cartItem.itemPrice")
    public abstract CartItemDto toCartItemDto(CartItem cartItem, SkuWithProductDto sku);
}
