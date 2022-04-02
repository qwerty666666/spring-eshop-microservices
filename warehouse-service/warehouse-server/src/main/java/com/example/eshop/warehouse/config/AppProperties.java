package com.example.eshop.warehouse.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@ConfigurationProperties("app")
@Getter
@Setter
@Validated
public class AppProperties {
    @NotNull
    private KafkaProperties kafka;

    @Getter
    @Setter
    public static class KafkaProperties {
        /**
         * Consumer Group ID
         */
        @NotEmpty
        private String consumerGroup;
    }
}
