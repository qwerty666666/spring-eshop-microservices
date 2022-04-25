package com.example.eshop.checkout.rest.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import java.net.URI;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UriUtils {
    /**
     * Template to Order Details Endpoint
     */
    public static final String ORDER_URI_TEMPLATE = "/api/orders/{id}";

    private final RestProperties restProperties;

    /**
     * Builds Absolute Uri to Order Endpoint
     */
    public URI buildOrderUri(UUID orderId) {
        return getServerUriComponentsBuilder()
                .path(ORDER_URI_TEMPLATE)
                .buildAndExpand(orderId.toString())
                .toUri();
    }

    private UriComponentsBuilder getServerUriComponentsBuilder() {
        return UriComponentsBuilder.newInstance()
                .scheme(restProperties.getSchema())
                .host(restProperties.getHost())
                .port(restProperties.getPort());
    }
}
