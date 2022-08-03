package com.example.eshop.apigateway.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Validated
@ConfigurationProperties("app")
@Getter
@Setter
public class AppProperties {
    /**
     * Url to swagger UI page
     */
    public static final String SWAGGER_UI_URL_PROPERTY = "${app.oas.url}";
    /**
     * Path to OpenAPI Specification
     */
    public static final String SWAGGER_SPEC_FILE_LOCATION = "${app.oas.spec-file-url}";

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
