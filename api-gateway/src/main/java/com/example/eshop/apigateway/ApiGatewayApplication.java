package com.example.eshop.apigateway;

import com.example.eshop.apigateway.filters.UpperBoundLimitRequestParameterFilterFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableEurekaClient
public class ApiGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder routeLocatorBuilder) {
        return routeLocatorBuilder.routes()
                .route("catalog", r -> r
                        .order(100)
                        .path("/api/{path:(?:products|categories)}/**")
                        .filters(f -> f
                                .filter(new UpperBoundLimitRequestParameterFilterFactory().apply(config -> config
                                        .setParameterName("per_page")
                                        .setMaxValue(30)
                                ))
                        )
                        .uri("lb://catalog-service")

                )
                .route("monolith", r -> r
                        .order(1000)
                        .path("/**")
                        .uri("lb://monolith")
                )
                .build();
    }
}
