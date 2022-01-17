package com.example.eshop.cart.config;

import com.example.eshop.customer.infrastructure.auth.UserDetailsImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetailsService;
import java.util.Collections;

@Configuration
public class AuthConfig {
    public final static String CUSTOMER_ID = "1";
    public final static String CUSTOMER_EMAIL = "test@test.test";

    @Bean
    @Primary
    public UserDetailsService userDetailsService() {
        return username -> new UserDetailsImpl(CUSTOMER_EMAIL, "pass", CUSTOMER_ID, Collections.emptyList());
    }
}
