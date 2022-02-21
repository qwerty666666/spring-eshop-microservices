package com.example.eshop.catalog;

import com.example.eshop.catalog.config.AppProperties;
import com.example.eshop.catalog.infrastructure.FileSystemFileStorage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication(exclude = {
        KafkaAutoConfiguration.class  // included in KafkaConfig
})
@EnableConfigurationProperties({ AppProperties.class, FileSystemFileStorage.Properties.class })
@EnableEurekaClient
public class CatalogApplication {
    public static void main(String[] args) {
        SpringApplication.run(CatalogApplication.class, args);
    }
}
