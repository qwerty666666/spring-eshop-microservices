package com.example.eshop.warehouse.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("app")
@Getter
@Setter
public class AppProperties {
    private KafkaProperties kafka;

    @Getter
    @Setter
    public static class KafkaProperties {
        /**
         * Consumer Group ID
         */
        private String consumerGroup;
    }
}
