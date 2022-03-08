package com.example.eshop.cart.rest.mappers;

import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EanMapper {
    default String toString(Ean ean) {
        return ean.toString();
    }
}
