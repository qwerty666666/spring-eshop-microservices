package com.example.eshop.catalog.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

@ConfigurationProperties("app")
@Getter
@Setter
public class AppProperties {
    /**
     * Base path to REST Endpoints
     */
    public static final String REST_API_BASE_PATH_PROPERTY = "${app.api-base-path}";

    /**
     * Hostname for building public URLs (e.g. https://my-service.com)
     */
    private String publicHostName = "";

    /**
     * Base path to api Endpoints (e.g. /api)
     */
    private String apiBasePath = "";

    /**
     * All image Urls are resolved relative to this path.
     */
    private String imagesBasePath = "";

    private KafkaProperties kafka;

    public void setPublicHostName(String publicHostName) {
        this.publicHostName = cleanUrl(publicHostName);
    }

    public void setApiBasePath(String apiBasePath) {
        this.apiBasePath = cleanUrl(apiBasePath);
    }

    public void setImagesBasePath(String imagesBasePath) {
        this.imagesBasePath = cleanUrl(imagesBasePath);
    }

    /**
     * Removes trailing slash and trim string
     */
    private String cleanUrl(String url) {
        String candidate = StringUtils.trimWhitespace(url);

        if (StringUtils.hasText(candidate) && candidate.endsWith("/")) {
            return candidate.substring(0, candidate.length() - 1);
        }

        return candidate;
    }

    @Getter
    @Setter
    public static class KafkaProperties {
        /**
         * Consumer Group ID
         */
        private String consumerGroup;
    }
}
