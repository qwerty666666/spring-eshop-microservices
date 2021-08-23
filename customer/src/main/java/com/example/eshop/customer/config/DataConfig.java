package com.example.eshop.customer.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration("customersDataConfig")
@EnableJpaRepositories(basePackages = "com.example.eshop.customer")
@EntityScan(basePackages = "com.example.eshop.customer")
public class DataConfig {
}
