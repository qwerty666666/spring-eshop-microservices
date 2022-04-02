package com.example.eshop.checkout.config;

import com.example.eshop.cart.client.CartServiceClient;
import com.example.eshop.cart.client.WebClientCartServiceClient;
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
public class CartServiceClientConfig {
    @Bean
    @RefreshScope
    public CartServiceClient cartService(AppProperties appProperties) {
        var webClient = cartWebClientBuilder(appProperties).build();

        return new WebClientCartServiceClient(webClient);
    }

    @Bean
    @RefreshScope
    @LoadBalanced
    public WebClient.Builder cartWebClientBuilder(AppProperties appProperties) {
        var cartServiceProperties = appProperties.getCartService();

        return WebClient.builder()
                .baseUrl("lb://shopping-cart-service/")
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient.create()
                                .responseTimeout(Duration.ofMillis(cartServiceProperties.getReadTimeoutMs()))
                                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, cartServiceProperties.getConnectTimeoutMs())
                ))
                // Relay Bearer token from current request.
                //
                // SecurityReactorContextConfiguration.SecurityReactorContextSubscriberRegistrar
                // use Hooks to save Authentication from current request to Reactor Context
                // for every request. Then ServletBearerExchangeFilterFunction takes this
                // Authentication and add token to bearer header.
                .filter(new ServletBearerExchangeFilterFunction());
    }
}
