package com.example.eshop.rest.mappers;

import com.example.eshop.catalog.domain.file.File;
import com.example.eshop.rest.dto.ImageDto;
import com.example.eshop.rest.staticresources.StaticResourceUriBuilder;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class ImageMapper {
    @Autowired
    private StaticResourceUriBuilder uriBuilder;

    public ImageDto toImageDto(File image) {
        var dto = new ImageDto();

        dto.setUrl(uriBuilder.buildImageUri(image.getLocation()));

        return dto;
    }
}
