package com.example.eshop.cart;

import com.example.eshop.cart.config.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
@EnableConfigurationProperties({ AppProperties.class })
public class ShoppingCartServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ShoppingCartServiceApplication.class, args);
    }
}
