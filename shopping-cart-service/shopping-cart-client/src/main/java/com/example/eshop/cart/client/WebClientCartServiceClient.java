package com.example.eshop.cart.client;

import com.example.eshop.cart.client.model.CartDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class WebClientCartServiceClient implements CartServiceClient {
    private static final String API_PREFIX = "/api";
    private static final String CART_URL = API_PREFIX + "/cart";
    private static final String CART_ITEMS_URL = CART_URL + "/items";

    private final WebClient webClient;

    @Override
    public Mono<CartDto> getCart(String customerId) {
        return webClient.get()
                .uri(CART_URL)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(CartDto.class);
    }

    @Override
    public Mono<CartDto> clear(String customerId) {
        return webClient.delete()
                .uri(CART_ITEMS_URL)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(CartDto.class);
    }
}
