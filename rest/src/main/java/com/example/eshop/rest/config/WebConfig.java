package com.example.eshop.rest.config;

import com.example.eshop.rest.AppProperties;
import com.example.eshop.rest.infrastructure.argumentresolvers.PageableSettingsArgumentResolver;
import com.example.eshop.rest.utils.UriUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.util.List;

@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE + 100)
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    private final PageableSettingsArgumentResolver pageableResolver;
    private final AppProperties appProperties;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(pageableResolver);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
                .addResourceHandler(UriUtils.IMAGES_BASE_PATH + "/*")
                .addResourceLocations(appProperties.getStaticResourcesLocation());
    }
}
