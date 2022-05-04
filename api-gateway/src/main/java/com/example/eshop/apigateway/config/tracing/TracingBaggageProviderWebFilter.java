package com.example.eshop.apigateway.config.tracing;

import com.example.eshop.auth.CustomJwtAuthentication;
import org.springframework.cloud.sleuth.instrument.web.WebFluxSleuthOperators;
import org.springframework.core.Ordered;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import java.util.Optional;

/**
 * {@link WebFilter} that add baggages to current tracing context
 */
@Component
public class TracingBaggageProviderWebFilter implements WebFilter, Ordered {
    @Override
    public int getOrder() {
        // We should run after security context is initialized
        return SecurityWebFiltersOrder.AUTHENTICATION.getOrder() + 1;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return Mono.deferContextual(context -> {
                    return ReactiveSecurityContextHolder.getContext()
                            .doOnNext(securityContext -> {
                                // Workaround to access current span
                                // https://github.com/spring-cloud/spring-cloud-sleuth/issues/1748
                                WebFluxSleuthOperators.withSpanInScope(context, () -> addCustomerIdBaggage(securityContext));
                            });
                })
                .then(chain.filter(exchange));
    }

    private void addCustomerIdBaggage(SecurityContext context) {
        Optional.ofNullable(context.getAuthentication())
                .map(CustomJwtAuthentication.class::cast)
                .map(CustomJwtAuthentication::getCustomerId)
                .ifPresent(BaggageFields.CUSTOMER_ID::updateValue);
    }
}
