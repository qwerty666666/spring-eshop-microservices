package com.example.eshop.rest.config;

import com.example.eshop.rest.infrastructure.web.argumentresolvers.PageableSettingsArgumentResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.util.List;

@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE + 100)
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    PageableSettingsArgumentResolver pageableResolver;
    @Autowired
    ApplicationContext context;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(pageableResolver);
    }
}
