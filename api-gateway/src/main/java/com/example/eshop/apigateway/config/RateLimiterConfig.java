package com.example.eshop.apigateway.config;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.factory.RequestRateLimiterGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;
import java.util.Optional;

@Configuration
public class RateLimiterConfig {
    @Bean
    GlobalFilter rateLimiterFilter() {
        // GatewayFilter can't be used as "default-filter" if we use routes API
        // (see https://github.com/spring-cloud/spring-cloud-gateway/issues/263),
        // therefore we convert it to GlobalFilter to apply it every route.
        var rateLimiterFilterFactory = new RequestRateLimiterGatewayFilterFactory(redisRateLimiter(), ipKeyResolver());

        var filter = rateLimiterFilterFactory.apply(config -> config
                .setDenyEmptyKey(false)
        );

        return filterToGlobalFilter(filter);
    }

    @Bean
    RedisRateLimiter redisRateLimiter() {
        return new RedisRateLimiter(10, 10, 1);
    }

    /**
     * KeyResolver that uses IP-address as key for rate limiting
     */
    @Bean
    KeyResolver ipKeyResolver() {
        return exchange -> {
            var ip = Optional.ofNullable(exchange.getRequest().getRemoteAddress())
                    .map(addr -> addr.getAddress().getHostAddress())
                    .orElse(null);

            return Mono.justOrEmpty(ip);
        };
    }

    /**
     * Converts {@link GatewayFilter} to {@link GlobalFilter}
     */
    private GlobalFilter filterToGlobalFilter(GatewayFilter filter) {
        return filter::filter;
    }
}
