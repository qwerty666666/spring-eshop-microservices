package com.example.eshop.rest.mappers;

import com.example.eshop.sharedkernel.domain.valueobject.Ean;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RestEanMapper {
    default String toString(Ean ean) {
        return ean.toString();
    }
}
