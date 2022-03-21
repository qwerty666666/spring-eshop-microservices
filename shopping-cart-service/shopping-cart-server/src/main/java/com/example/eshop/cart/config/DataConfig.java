package com.example.eshop.cart.config;

import com.example.eshop.sharedkernel.infrastructure.dal.SimpleNaturalIdRepositoryImpl;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.example.eshop.cart",
        repositoryBaseClass = SimpleNaturalIdRepositoryImpl.class
)
public class DataConfig {
}
