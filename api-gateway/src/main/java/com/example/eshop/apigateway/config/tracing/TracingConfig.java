package com.example.eshop.apigateway.config.tracing;

import brave.baggage.CorrelationScopeConfig.SingleCorrelationField;
import brave.context.slf4j.MDCScopeDecorator;
import brave.propagation.CurrentTraceContext.ScopeDecorator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TracingConfig {
    @Bean
    public ScopeDecorator mdcScopeDecorator() {
        return MDCScopeDecorator.newBuilder()
                .clear()
                // We need this to apply baggage in current scope
                // https://docs.spring.io/spring-cloud-sleuth/docs/current-SNAPSHOT/reference/html/project-features.html#features-baggage
                .add(SingleCorrelationField.newBuilder(BaggageFields.CUSTOMER_ID)
                        .flushOnUpdate()
                        .build())
                .build();
    }
}
