package com.example.eshop.warehouse.config;

import org.springframework.boot.autoconfigure.transaction.PlatformTransactionManagerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;

/**
 * Configurations for Spring Transaction.
 */
@Configuration
public class TxConfig {
    /**
     * Configure {@link PlatformTransactionManager}.
     */
    @Bean
    public PlatformTransactionManagerCustomizer<AbstractPlatformTransactionManager> txManagerCustomizer() {
        return txManager -> txManager
                // force validating isolation lvl for inner transactions
                .setValidateExistingTransaction(true);
    }
}
