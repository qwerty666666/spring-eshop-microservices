package com.example.eshop.rest.utils;

import com.example.eshop.rest.AppProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import java.net.URI;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UriUtils {
    /**
     * Base path to REST Endpoints
     */
    public static final String API_BASE_PATH_PROPERTY = "${app.api-base-path}";
    /**
     * Base path to REST Endpoints
     */
    public static final String SWAGGER_UI_URL_PROPERTY = "${app.api-base-path}";
    /**
     * Template to Order Details Endpoint
     */
    public static final String ORDER_URI_TEMPLATE = "/orders/{id}";

    /**
     * Base path to static images
     */
    public static final String IMAGES_BASE_PATH = "/images";

    private final AppProperties appProperties;

    /**
     * Builds Absolute Uri to Order Endpoint
     */
    public URI buildOrderUri(UUID orderId) {
        return getServerUriComponentsBuilder()
                .path(appProperties.getApiBasePath() + ORDER_URI_TEMPLATE)
                .buildAndExpand(orderId.toString())
                .toUri();
    }

    /**
     * Builds Absolute Uri to images Endpoint
     *
     * @param location path to the image
     */
    public URI buildImageUri(String location) {
        return getServerUriComponentsBuilder()
                .path(IMAGES_BASE_PATH + "/" + location)
                .build()
                .toUri();
    }

    private UriComponentsBuilder getServerUriComponentsBuilder() {
        return UriComponentsBuilder.newInstance()
                .scheme(appProperties.getSchema())
                .host(appProperties.getHost())
                .port(appProperties.getPort());
    }
}
