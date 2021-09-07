package com.example.eshop.rest.mappers;

import com.example.eshop.cart.application.usecases.cartquery.dto.CartDto;
import com.example.eshop.rest.resources.cart.CartItemResource;
import com.example.eshop.rest.resources.cart.CartResource;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = { EanMapper.class })
public interface CartMapper {
    default CartResource toCartResource(CartDto cart) {
        var items = cart.items()
                .stream()
                .map(CartItemResource::new)
                .toList();

        return new CartResource(cart.id(), items);
    }
}
