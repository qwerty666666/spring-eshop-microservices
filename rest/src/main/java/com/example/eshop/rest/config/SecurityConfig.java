package com.example.eshop.rest.config;

import com.example.eshop.auth.JwtAuthenticationConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration("rest-securityConfig")
@Import(com.example.eshop.customer.config.SecurityConfig.class)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                // disable default
                .csrf().disable()
                .formLogin().disable()
                .logout().disable()
                // session
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // authorization
                .authorizeRequests(requests -> requests
                        // /api Endpoints
                        .antMatchers("/api/categories/**").permitAll()
                        .antMatchers("/api/products/**").permitAll()
                        .antMatchers(HttpMethod.POST, "/api/customers").permitAll()
                        .antMatchers("/api/**").authenticated()
                        // Actuator Endpoints
//                        .requestMatchers(EndpointRequest.toAnyEndpoint()).hasRole(Role.ADMIN)
                        // Others
                        .anyRequest().permitAll()
                )
                .oauth2ResourceServer(resourceServer -> resourceServer
                        .jwt()
                        .jwtAuthenticationConverter(new JwtAuthenticationConverter())
                );
    }
}
