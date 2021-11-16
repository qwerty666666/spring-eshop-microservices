package com.example.eshop.rest;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.validation.annotation.Validated;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Validated
@ConfigurationProperties("app")
@Getter
@Setter
public class AppProperties {
    /**
     * Service schema (e.g. http/https)
     */
    private String schema = "http";

    /**
     * Service host (e.g. localhost)
     */
    @NotEmpty
    private String host;

    /**
     * Service port (e.g. 8080)
     */
    @NotEmpty
    private String port;

    /**
     * Base path to api Endpoints (e.g. /api)
     */
    private String apiBasePath;

    /**
     * Static Resources location (e.g. classpath:/META-INF/public-web-resources/)
     */
    @NotEmpty
    private String staticResourcesLocation;

    @NestedConfigurationProperty
    @NotNull
    private OasProperties oas;

    @Setter
    @Getter
    public static class OasProperties {
        /**
         * Url for Swagger UI Endpoint
         */
        @NotEmpty
        private String url;
        /**
         * Url of OpenAPI specification file
         */
        @NotEmpty
        private String specFileUrl;
    }
}
