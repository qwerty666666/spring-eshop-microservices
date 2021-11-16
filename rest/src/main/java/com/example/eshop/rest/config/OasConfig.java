package com.example.eshop.rest.config;

import com.example.eshop.rest.AppProperties;
import com.example.eshop.rest.utils.SwaggerUiResourceTransformer;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class OasConfig implements WebMvcConfigurer {
    private final AppProperties appProperties;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
                .addResourceHandler(appProperties.getOas().getUrl() + "/*")
                .addResourceLocations("classpath:/META-INF/resources/webjars/swagger-ui/4.0.1/")
                .resourceChain(false)
                .addTransformer(new SwaggerUiResourceTransformer(appProperties.getOas().getSpecFileUrl()));
    }
}
