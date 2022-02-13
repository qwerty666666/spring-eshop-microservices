package com.example.eshop.rest.controllers;

import com.example.eshop.rest.api.OrderApi;
import com.example.eshop.rest.controllers.base.BaseController;
import com.example.eshop.rest.controllers.base.BasicErrorBuilder;
import com.example.eshop.rest.dto.BasicErrorDto;
import com.example.eshop.rest.dto.OrderDto;
import com.example.eshop.rest.dto.PagedOrderListDto;
import com.example.eshop.rest.mappers.RestOrderMapper;
import com.example.eshop.rest.utils.UriUtils;
import com.example.eshop.sales.application.services.queryorder.OrderNotFoundException;
import com.example.eshop.sales.application.services.queryorder.QueryOrderService;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
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
import java.util.Locale;
import java.util.UUID;

@RestController
@RequestMapping(UriUtils.API_BASE_PATH_PROPERTY)
@RequiredArgsConstructor
@Getter(AccessLevel.PROTECTED)  // for access to autowired fields from @ExceptionHandler
public class OrderController extends BaseController implements OrderApi {
    private final QueryOrderService queryOrderService;
    private final RestOrderMapper orderMapper;
    private final MessageSource messageSource;

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    private BasicErrorDto handleOrderNotFoundException(OrderNotFoundException e, Locale locale) {
        return BasicErrorBuilder.newInstance()
                .setStatus(HttpStatus.NOT_FOUND)
                .setDetail(getMessageSource().getMessage("orderNotFound", null, locale))
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
