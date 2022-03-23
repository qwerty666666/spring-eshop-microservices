package com.example.eshop.checkout.rest.utils;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;
import javax.validation.constraints.NotEmpty;

@Validated
@ConfigurationProperties("rest")
@Getter
@Setter
public class RestProperties {
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
}
