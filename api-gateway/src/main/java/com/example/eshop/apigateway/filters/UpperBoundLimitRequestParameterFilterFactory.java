package com.example.eshop.apigateway.filters;

import com.example.eshop.apigateway.filters.UpperBoundLimitRequestParameterFilterFactory.Config;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import static org.springframework.cloud.gateway.support.GatewayToStringStyler.filterToStringCreator;

/**
 * Restrict upper bound for int query parameter.
 */
public class UpperBoundLimitRequestParameterFilterFactory extends AbstractGatewayFilterFactory<Config> {
    public UpperBoundLimitRequestParameterFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return new GatewayFilter() {
            @Override
            public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
                config.validate();

                var request = exchange.getRequest();
                var pageSize = getPageSizeFromRequest(exchange.getRequest(), config);

                if (pageSize != null && pageSize > config.getMaxValue()) {
                    var newUri = UriComponentsBuilder.fromUri(request.getURI())
                            .replaceQueryParam(config.getParameterName(), config.getMaxValue())
                            .build(true)
                            .toUri();

                    request = request.mutate().uri(newUri).build();

                    exchange = exchange.mutate().request(request).build();
                }

                return chain.filter(exchange);
            }

            @Override
            public String toString() {
                return filterToStringCreator(UpperBoundLimitRequestParameterFilterFactory.this)
                        .append("parameterName", config.getParameterName())
                        .append("maxValue", config.getMaxValue())
                        .toString();
            }
        };
    }

    @Nullable
    private Integer getPageSizeFromRequest(ServerHttpRequest request, Config config) {
        var pageSize = request.getQueryParams().getFirst(config.getParameterName());

        if (pageSize == null) {
            return null;
        }

        try {
            return Integer.parseInt(pageSize);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Accessors(chain = true)
    public static class Config {
        private String parameterName;
        private int maxValue;

        public void validate() {
            Assert.hasText(parameterName, "parameterName must be non empty");
        }
    }
}
