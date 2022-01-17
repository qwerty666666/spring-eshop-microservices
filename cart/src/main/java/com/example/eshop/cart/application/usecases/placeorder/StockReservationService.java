package com.example.eshop.cart.application.usecases.placeorder;

import com.example.eshop.cart.domain.checkout.order.Order;
import com.example.eshop.warehouse.client.reservationresult.ReservationResult;

public interface StockReservationService {
    /**
     * Reserve stocks in warehouse for the given {@code order}
     *
     * @throws StockReservationException if we can't get result for stock reservation
     *         for some reason
     */
    ReservationResult reserve(Order order);
}
