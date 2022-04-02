package com.example.eshop.cart.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@ConfigurationProperties("app")
@Getter
@Setter
@Validated
public class AppProperties {
    /**
     * Properties for catalog microservice
     */
    @NotNull
    private CatalogServiceProperties catalogService;

    /**
     * Properties for catalog microservice
     */
    @Getter
    @Setter
    public static class CatalogServiceProperties {
        /**
         * Http client Connection Timeout in ms
         */
        @Positive
        private int connectTimeoutMs;
        /**
         * Http client Read Timeout in ms
         */
        @Positive
        private int readTimeoutMs;
    }
}
