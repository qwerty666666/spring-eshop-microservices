package com.example.eshop.rest;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import javax.validation.constraints.NotEmpty;

@ConfigurationProperties("app")
@Getter
@Setter
public class AppProperties {
    /**
     * Service schema (http/https)
     */
    private String schema = "http";

    /**
     * Service host (localhost)
     */
    @NotEmpty
    private String host;

    /**
     * Service (8080)
     */
    @NotEmpty
    private String port;

    /**
     * Base path to api Endpoints (/api)
     */
    private String apiBasePath = "/api";

    /**
     * Static Resources location (classpath:/META-INF/public-web-resources/)
     */
    @NotEmpty
    private String staticResourcesLocation;
}
