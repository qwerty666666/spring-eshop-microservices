package com.example.eshop.order.rest.controllers;

import com.example.eshop.catalog.client.api.model.BasicErrorDto;
import com.example.eshop.localizer.Localizer;
import com.example.eshop.order.application.services.queryorder.OrderNotFoundException;
import com.example.eshop.order.application.services.queryorder.QueryOrderService;
import com.example.eshop.order.client.api.model.OrderDto;
import com.example.eshop.order.client.api.model.PagedOrderListDto;
import com.example.eshop.order.rest.api.OrderApi;
import com.example.eshop.order.rest.mappers.OrderMapper;
import com.example.eshop.order.rest.utils.BasicErrorBuilder;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Getter(AccessLevel.PROTECTED)  // for access to autowired fields from @ExceptionHandler
public class OrderController extends BaseController implements OrderApi {
    private final QueryOrderService queryOrderService;
    private final OrderMapper orderMapper;
    private final Localizer localizer;

    @ExceptionHandler(OrderNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    private BasicErrorDto handleOrderNotFoundException() {
        return BasicErrorBuilder.newInstance()
                .setStatus(HttpStatus.NOT_FOUND)
                .setDetail(getLocalizer().getMessage("orderNotFound"))
                .build();
    }

    @Override
    public ResponseEntity<OrderDto> getOrder(UUID orderId) {
        var order = queryOrderService.getById(orderId);

        var authenticatedCustomerId = getCurrentAuthenticationOrFail().getCustomerId();
        if (!order.getCustomerId().equals(authenticatedCustomerId)) {
            throw new AccessDeniedException("Authenticated customer have no permission to view order " + orderId);
        }

        return ResponseEntity.ok(orderMapper.toOrderDto(order));
    }

    @Override
    public ResponseEntity<PagedOrderListDto> getOrderList(Integer perPage, Integer page) {
        var userDetails = getCurrentAuthenticationOrFail();
        var pageable = PageRequest.ofSize(perPage)
                .withPage(page - 1)
                .withSort(Sort.by(new Order(Direction.DESC, "creationDate")));

        var orders = queryOrderService.getForCustomer(userDetails.getCustomerId(), pageable);

        return ResponseEntity.ok(orderMapper.toPagedOrderListDto(orders));
    }
}
