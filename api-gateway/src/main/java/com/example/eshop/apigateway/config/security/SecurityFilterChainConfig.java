package com.example.eshop.apigateway.config.security;

import com.example.eshop.auth.ReactiveJwtAuthenticationConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * Set up {@link SecurityWebFilterChain}
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityFilterChainConfig {
    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
                // Disable default security
                .httpBasic().disable()
                .formLogin().disable()
                .csrf().disable()
                .logout().disable()
                // Authorize
                .authorizeExchange().anyExchange().permitAll()
                .and()
                .oauth2ResourceServer(resourceServer -> resourceServer
                        .jwt()
                        .jwtAuthenticationConverter(new ReactiveJwtAuthenticationConverter())
                )
                .build();
    }
}
