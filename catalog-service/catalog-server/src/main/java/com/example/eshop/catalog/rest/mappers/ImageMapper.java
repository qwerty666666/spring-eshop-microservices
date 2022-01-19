package com.example.eshop.catalog.rest.mappers;

import com.example.eshop.catalog.client.api.model.Image;
import com.example.eshop.catalog.domain.file.File;
import com.example.eshop.catalog.utils.UriBuilder;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class ImageMapper {
    @Autowired
    private UriBuilder uriBuilder;

    public Image toImageDto(File image) {
        var dto = new Image();

        var url = image.isExternal() ? image.getLocation() : uriBuilder.buildImageUri(image.getLocation()).toString();
        dto.setUrl(url);

        return dto;
    }
}
