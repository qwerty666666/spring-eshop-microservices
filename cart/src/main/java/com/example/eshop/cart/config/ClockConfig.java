package com.example.eshop.cart.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.time.Clock;

@Configuration
public class ClockConfig {
    // We use Clock for testing DateTime API
    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }
}
