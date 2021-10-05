package com.example.eshop.rest.staticresources;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;
import javax.validation.constraints.NotEmpty;

@ConfigurationProperties(prefix = "resources")
@Validated
@Getter
@Setter
public class ResourceProperties {
    /**
     * Static Resources location. For example "classpath:/META-INF/public-web-resources/"
     */
    @NotEmpty
    private String location;
}
