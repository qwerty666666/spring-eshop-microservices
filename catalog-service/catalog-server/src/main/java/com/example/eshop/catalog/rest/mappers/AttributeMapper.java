package com.example.eshop.catalog.rest.mappers;

import com.example.eshop.catalog.client.api.model.AttributeDto;
import com.example.eshop.catalog.domain.product.AttributeValue;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AttributeMapper {
    @Mapping(target = "name", source = "attribute.name")
    AttributeDto toAttributeDto(AttributeValue attributeValue);
}
