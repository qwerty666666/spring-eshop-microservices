package com.example.eshop.order.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import static org.mockito.Mockito.mock;

@Configuration
@Profile("test")
public class AuthConfig {
    public final static String CUSTOMER_ID = "1";
    public final static String CUSTOMER_EMAIL = "test@test.test";

    @Bean
    public JwtDecoder jwtDecoder() {
        return mock(JwtDecoder.class);
    }
}
