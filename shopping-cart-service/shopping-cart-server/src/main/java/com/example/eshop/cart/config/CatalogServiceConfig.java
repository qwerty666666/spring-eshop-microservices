package com.example.eshop.cart.config;

import com.example.eshop.catalog.client.CatalogServiceClient;
import com.example.eshop.catalog.client.WebClientCatalogServiceClient;
import io.netty.channel.ChannelOption;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.security.oauth2.server.resource.web.reactive.function.client.ServletBearerExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import java.time.Duration;

@Configuration
public class CatalogServiceConfig {
    @Bean
    @RefreshScope
    public CatalogServiceClient catalogService(AppProperties appProperties) {
        var webClient = catalogWebClientBuilder(appProperties).build();

        return new WebClientCatalogServiceClient(webClient);
    }

    @Bean
    @RefreshScope
    @LoadBalanced
    public WebClient.Builder catalogWebClientBuilder(AppProperties appProperties) {
        var catalogServiceProperties = appProperties.getCatalogService();

        return WebClient.builder()
                .baseUrl("lb://catalog-service/")
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient.create()
                                .responseTimeout(Duration.ofMillis(catalogServiceProperties.getReadTimeoutMs()))
                                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, catalogServiceProperties.getConnectTimeoutMs())
                ))
                .filter(new ServletBearerExchangeFilterFunction());
    }
}
