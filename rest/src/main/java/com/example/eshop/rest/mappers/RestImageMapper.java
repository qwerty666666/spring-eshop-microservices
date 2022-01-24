package com.example.eshop.rest.mappers;

import com.example.eshop.catalog.domain.file.File;
import com.example.eshop.rest.dto.ImageDto;
import com.example.eshop.rest.utils.UriFactory;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class RestImageMapper {
    @Autowired
    private UriFactory uriFactory;

    public ImageDto toImageDto(File image) {
        var dto = new ImageDto();

        var url = image.isExternal() ? image.getLocation() : uriFactory.buildImageUri(image.getLocation()).toString();
        dto.setUrl(url);

        return dto;
    }
}
