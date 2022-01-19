package com.example.eshop.catalog.utils;

import com.example.eshop.catalog.config.AppProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import java.net.URI;

@Component
@RequiredArgsConstructor
public class UriBuilder {
    private final AppProperties appProperties;

    /**
     * Builds Absolute Uri to images Endpoint
     *
     * @param location path to the image
     */
    public URI buildImageUri(String location) {
        return UriComponentsBuilder.fromHttpUrl(appProperties.getPublicHostName())
                .path(appProperties.getImagesBasePath() + '/' + location)
                .build()
                .toUri();
    }
}
