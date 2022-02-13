package com.example.eshop.cart.application.usecases.placeorder;

import com.example.eshop.cart.domain.checkout.order.Order;
import com.example.eshop.checkout.client.CheckoutApi;
import com.example.eshop.checkout.client.order.OrderDto;
import com.example.eshop.warehouse.client.reservationresult.ReservationResult;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class StockReservationServiceImpl implements StockReservationService {
    private static final Duration REPLY_TIMEOUT = Duration.ofSeconds(10);

    private final ReplyingKafkaTemplate<String, OrderDto, ReservationResult> kafkaTemplate;
    private final OrderMapper orderMapper;

    @Override
    @SneakyThrows
    public ReservationResult reserve(Order order) {
        var dto = orderMapper.toOrderDto(order);
        var key = order.getId().toString();

        var producerRecord = new ProducerRecord<>(CheckoutApi.RESERVE_STOCKS_TOPIC, key, dto);

        try {
            return kafkaTemplate.sendAndReceive(producerRecord, REPLY_TIMEOUT)
                    .get(REPLY_TIMEOUT.toSeconds(), TimeUnit.SECONDS)
                    .value();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new StockReservationException(e);
        } catch (Exception e) {
            throw new StockReservationException(e);
        }
    }
}
