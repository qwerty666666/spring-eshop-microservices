package com.example.eshop.cart.config;

import com.example.eshop.catalog.client.ApiClient;
import com.example.eshop.catalog.client.api.ProductsApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class CatalogConfig {
    @Bean
    public ProductsApi productsApi(ApiClient apiClient) {
        return new ProductsApi(apiClient);
    }

    @Bean
    public ApiClient apiClient() {
        return new ApiClient(WebClient.create())
                // TODO replace with Service Discovery
                .setBasePath("http://localhost:8087/api/");
    }
}
