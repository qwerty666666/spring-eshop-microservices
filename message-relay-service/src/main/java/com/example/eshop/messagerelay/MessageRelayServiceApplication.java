package com.example.eshop.messagerelay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication(exclude = {
        DataSourceAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class,
})
@EnableConfigurationProperties(OutboxProperties.class)
public class MessageRelayServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(MessageRelayServiceApplication.class, args);
    }
}
