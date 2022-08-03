package com.example.eshop.cart.client;

import com.example.eshop.cart.client.model.CartDto;
import reactor.core.publisher.Mono;

/**
 * Gateway to Cart microservice REST API
 */
public interface CartServiceClient {
    /**
     * Find Cart by customer ID
     */
    Mono<CartDto> getCart(String customerId);

    /**
     * Clear customer's Cart
     */
    Mono<CartDto> clear(String customerId);
}
