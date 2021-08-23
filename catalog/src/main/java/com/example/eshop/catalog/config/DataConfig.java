package com.example.eshop.catalog.config;

import com.cosium.spring.data.jpa.entity.graph.repository.support.EntityGraphJpaRepositoryFactoryBean;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import javax.persistence.Entity;

@Configuration("catalogDataConfig")
@EnableJpaRepositories(
        basePackages = "com.example.eshop.catalog",
        repositoryFactoryBeanClass = EntityGraphJpaRepositoryFactoryBean.class
)
@EntityScan(basePackages = "com.example.eshop.catalog")
public class DataConfig {
}
