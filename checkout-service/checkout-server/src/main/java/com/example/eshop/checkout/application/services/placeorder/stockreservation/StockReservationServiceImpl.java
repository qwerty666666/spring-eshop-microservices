package com.example.eshop.checkout.application.services.placeorder.stockreservation;

import com.example.eshop.checkout.application.services.PublishEventException;
import com.example.eshop.checkout.client.CheckoutApi;
import com.example.eshop.checkout.client.events.orderplacedevent.OrderDto;
import com.example.eshop.checkout.config.AppProperties;
import com.example.eshop.warehouse.client.reservationresult.ReservationResult;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Service
@RefreshScope
@RequiredArgsConstructor
public class StockReservationServiceImpl implements StockReservationService {
    private final ReplyingKafkaTemplate<String, OrderDto, ReservationResult> kafkaTemplate;
    private final AppProperties appProperties;

    @Override
    public ReservationResult reserve(OrderDto order) {
        var key = order.id().toString();
        var producerRecord = new ProducerRecord<>(CheckoutApi.RESERVE_STOCKS_TOPIC, key, order);
        var timeout = appProperties.getKafka().getStockReservationReplyTimeoutMs();

        try {
            return kafkaTemplate.sendAndReceive(producerRecord, Duration.ofMillis(timeout))
                    .get(timeout, TimeUnit.MILLISECONDS)
                    .value();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new PublishEventException(e);
        } catch (Exception e) {
            throw new PublishEventException(e);
        }
    }
}
