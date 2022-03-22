package com.example.eshop.checkout.config;

import com.example.eshop.cart.client.CartServiceClient;
import com.example.eshop.cart.client.WebClientCartServiceClient;
import io.netty.channel.ChannelOption;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
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
    public CartServiceClient cartService() {
        var webClient = cartWebClientBuilder().build();

        return new WebClientCartServiceClient(webClient);
    }

    @Bean
    @LoadBalanced
    public WebClient.Builder cartWebClientBuilder() {
        return WebClient.builder()
                .baseUrl("lb://shopping-cart-service/")
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient.create()
                                .wiretap(true)
                                .responseTimeout(Duration.ofMillis(3000))
                                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
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
