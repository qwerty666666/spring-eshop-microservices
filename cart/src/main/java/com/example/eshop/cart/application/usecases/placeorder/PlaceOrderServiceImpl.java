package com.example.eshop.cart.application.usecases.placeorder;

import com.example.eshop.cart.application.usecases.cartquery.CartNotFoundException;
import com.example.eshop.cart.application.usecases.checkout.OrderDto;
import com.example.eshop.cart.domain.cart.Cart;
import com.example.eshop.cart.domain.cart.CartRepository;
import com.example.eshop.cart.domain.checkout.placeorder.PlaceOrderDto;
import com.example.eshop.cart.domain.checkout.order.Order;
import com.example.eshop.cart.domain.checkout.order.OrderFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PlaceOrderServiceImpl implements PlaceOrderService {
    private final CartRepository cartRepository;
    private final com.example.eshop.cart.domain.checkout.placeorder.PlaceOrderService placeOrderService;
    private final OrderFactory orderFactory;

    @Override
    @PreAuthorize("#orderDto.customerId() == principal.getCustomerId()")
    @Transactional
    public Order place(OrderDto orderDto) {
        // create order with customer's cart
        var order = createOrder(orderDto);

        // place order
        placeOrderService.place(order);

        // and clear customer's cart
        clearCart(orderDto.customerId());

        return order;
    }

    private Order createOrder(OrderDto orderDto) {
        var cart = getCart(orderDto.customerId());

        var createOrderDto = new PlaceOrderDto(orderDto.customerId(), cart, orderDto.address(),
                orderDto.deliveryServiceId(), orderDto.paymentServiceId());

        return orderFactory.create(createOrderDto);
    }

    private Cart getCart(String customerId) {
        return cartRepository.findByNaturalId(customerId)
                .orElseThrow(() -> new CartNotFoundException("Customer " + customerId + " has no Cart"));
    }

    private void clearCart(String customerId) {
        var cart = getCart(customerId);

        cart.clear();
    }
}
