package com.example.eshop.cart.config;

import com.example.eshop.catalog.client.ApiClient;
import com.example.eshop.catalog.client.api.ProductsApi;
import com.example.eshop.catalog.client.cataloggateway.CatalogGateway;
import com.example.eshop.catalog.client.cataloggateway.CatalogGatewayImpl;
import io.netty.channel.ChannelOption;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import java.time.Duration;

@Configuration
public class CatalogConfig {
    @Bean
    public CatalogGateway catalogGateway() {
        var webClient = catalogWebClientBuilder().build();
        var apiClient = new ApiClient(webClient)
                .setBasePath("lb://catalog-service/api/");
        var productsApi = new ProductsApi(apiClient);

        return new CatalogGatewayImpl(productsApi);
    }

    @Bean
    @LoadBalanced
    public WebClient.Builder catalogWebClientBuilder() {
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient.create()
                                .responseTimeout(Duration.ofMillis(3000))
                                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
                ));
    }
}
