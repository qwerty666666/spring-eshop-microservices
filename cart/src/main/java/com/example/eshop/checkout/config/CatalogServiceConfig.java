package com.example.eshop.checkout.config;

import com.example.eshop.catalog.client.CatalogService;
import com.example.eshop.catalog.client.CatalogServiceImpl;
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
    public CatalogService catalogService() {
        var webClient = catalogWebClientBuilder().build();

        return new CatalogServiceImpl(webClient);
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
