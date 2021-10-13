package com.example.eshop.sales.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration("sales-DataConfig")
@EnableJpaRepositories(basePackages = "com.example.eshop.sales")
@EntityScan(basePackages = "com.example.eshop.sales")
public class DataConfig {
}
