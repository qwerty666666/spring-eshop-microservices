package com.example.eshop.rest.staticresources;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Component
public class StaticResourceUriBuilder {
    public static final String IMAGES_PATH = "/images/";

    /**
     * Builds full Url to images Endpoint
     *
     * @param location path to the image
     */
    public String buildImageUri(String location) {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(IMAGES_PATH + "/" + location)
                .toUriString();
    }
}
