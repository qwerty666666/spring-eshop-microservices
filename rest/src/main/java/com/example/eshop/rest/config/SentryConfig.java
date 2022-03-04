package com.example.eshop.rest.config;

import com.example.eshop.auth.AuthUtils;
import com.example.eshop.auth.CustomJwtAuthentication;
import io.sentry.protocol.User;
import io.sentry.spring.SentryUserProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SentryConfig {
    /**
     * Pass customerId as User ID to Sentry Events
     */
    @Bean
    public SentryUserProvider registeringCustomerIdSentryUserProvider() {
        return () -> {
            var user = new User();

            AuthUtils.getCurrentUserDetails()
                    .map(CustomJwtAuthentication::getCustomerId)
                    .ifPresent(user::setId);

            return user;
        };
    }
}
