package com.example.eshop.rest.config;

import com.example.eshop.customer.infrastructure.auth.UserDetailsImpl;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetailsService;

@TestConfiguration
public class AuthConfig {
    public final static String CUSTOMER_ID = "1";
    public final static String CUSTOMER_EMAIL = "test@test.test";
    public final static String CUSTOMER_PASSWORD = "pass";

    @Bean
    @Primary
    public UserDetailsService userDetailsService() {
        return username -> new UserDetailsImpl(CUSTOMER_EMAIL, CUSTOMER_PASSWORD, CUSTOMER_ID);
    }
}