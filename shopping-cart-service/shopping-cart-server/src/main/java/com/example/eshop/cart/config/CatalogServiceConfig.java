package com.example.eshop.cart.config;

import com.example.eshop.catalog.client.CatalogServiceClient;
import com.example.eshop.catalog.client.WebClientCatalogServiceClient;
import io.netty.channel.ChannelOption;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import java.time.Duration;

@Configuration
public class CatalogServiceConfig {
    @Bean
    public CatalogServiceClient catalogService() {
        var webClient = catalogWebClientBuilder().build();

        return new WebClientCatalogServiceClient(webClient);
    }

    @Bean
    @LoadBalanced
    public WebClient.Builder catalogWebClientBuilder() {
        return WebClient.builder()
                .baseUrl("lb://catalog-service/")
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient.create()
                                .responseTimeout(Duration.ofMillis(3000))
                                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
                ));
    }
}
