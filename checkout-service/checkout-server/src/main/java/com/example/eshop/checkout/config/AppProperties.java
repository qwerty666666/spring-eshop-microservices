package com.example.eshop.checkout.config;

import com.example.eshop.checkout.client.events.orderplacedevent.OrderPlacedEvent;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@ConfigurationProperties("app")
@Getter
@Setter
@Validated
public class AppProperties {
    /**
     * Properties for cart microservice
     */
    @NotNull
    private CartServiceProperties cartService;
    /**
     * Properties for Kafka Client
     */
    @NotNull
    private KafkaProperties kafka;

    /**
     * Properties for cart microservice
     */
    @Getter
    @Setter
    public static class CartServiceProperties {
        /**
         * Http client Connection Timeout in ms
         */
        @Positive
        private int connectTimeoutMs;
        /**
         * Http client Read Timeout in ms
         */
        @Positive
        private int readTimeoutMs;
    }

    /**
     * Properties for Kafka Client
     */
    @Getter
    @Setter
    public static class KafkaProperties {
        /**
         * Timeout for sending {@link OrderPlacedEvent}
         */
        @Positive
        private int orderPlacedEventPublishedTimeoutMs;
        /**
         * Timeout for await reply for stock reservation
         */
        @Positive
        private int stockReservationReplyTimeoutMs;
    }
}
