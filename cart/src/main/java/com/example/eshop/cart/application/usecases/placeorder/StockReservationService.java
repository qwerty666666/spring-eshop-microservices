package com.example.eshop.cart.application.usecases.placeorder;

import com.example.eshop.checkout.client.events.orderplacedevent.OrderDto;
import com.example.eshop.warehouse.client.reservationresult.ReservationResult;

public interface StockReservationService {
    /**
     * Reserve stocks in warehouse for the given {@code order}
     *
     * @throws PublishEventException if we can't get result for stock reservation
     *         for some reason
     */
    ReservationResult reserve(OrderDto order);
}
