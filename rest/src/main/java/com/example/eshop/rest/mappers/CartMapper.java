package com.example.eshop.rest.mappers;

import com.example.eshop.rest.dto.CartDto;
import com.example.eshop.rest.dto.CartItemDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = { EanMapper.class })
public interface CartMapper {
    CartDto toCartDto(com.example.eshop.cart.application.usecases.cartquery.dto.CartDto cart);

    CartItemDto toCartItemDto(com.example.eshop.cart.application.usecases.cartquery.dto.CartItemDto cartItem);
}
