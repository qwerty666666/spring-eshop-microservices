package com.example.eshop.apigateway.config;

import com.example.eshop.apigateway.filters.UpperBoundLimitRequestParameterFilterFactory;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RoutingConfig {
    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder routeLocatorBuilder) {
        return routeLocatorBuilder.routes()
                .route("catalog", r -> r
                        .order(100)
                        .path("/api/{path:(?:products|categories|sku)}/**")
                        .filters(f -> f
                                .filter(new UpperBoundLimitRequestParameterFilterFactory().apply(config -> config
                                        .setParameterName("per_page")
                                        .setMaxValue(30)
                                ))
                        )
                        .uri("lb://catalog-service")

                )
                .route("orders", r -> r
                        .order(200)
                        .path("/api/orders/**")
                        .filters(f -> f
                                .filter(new UpperBoundLimitRequestParameterFilterFactory().apply(config -> config
                                        .setParameterName("per_page")
                                        .setMaxValue(30)
                                ))
                        )
                        .uri("lb://orders-service")
                )
                .route("cart", r -> r
                        .order(300)
                        .path("/api/cart/**")
                        .uri("lb://shopping-cart-service")
                )
                .route("checkout", r -> r
                        .order(400)
                        .path("/api/checkout/**")
                        .uri("lb://checkout-service")
                )
                .build();
    }
}
