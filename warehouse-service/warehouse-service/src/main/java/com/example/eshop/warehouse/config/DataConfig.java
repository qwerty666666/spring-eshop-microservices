package com.example.eshop.warehouse.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.example.eshop.warehouse.domain")
@EntityScan(basePackages = "com.example.eshop.warehouse.domain")
public class DataConfig {
}
