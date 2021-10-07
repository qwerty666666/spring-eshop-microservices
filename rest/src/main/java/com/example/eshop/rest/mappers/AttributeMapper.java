package com.example.eshop.rest.mappers;

import com.example.eshop.catalog.domain.product.AttributeValue;
import com.example.eshop.rest.dto.AttributeDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AttributeMapper {
    @Mapping(target = "name", source = "attribute.name")
    AttributeDto toAttributeDto(AttributeValue attributeValue);
}
