package com.example.eshop.rest.controllers;

import com.example.eshop.rest.api.OrderApi;
import com.example.eshop.rest.controllers.base.BaseController;
import com.example.eshop.rest.dto.PagedOrderListDto;
import com.example.eshop.rest.mappers.OrderMapper;
import com.example.eshop.sales.application.services.queryorder.QueryOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class OrderController extends BaseController implements OrderApi {
    private final QueryOrderService queryOrderService;
    private final OrderMapper orderMapper;

    @Override
    public ResponseEntity<PagedOrderListDto> getOrderList(Integer perPage, Integer page) {
        var userDetails = getAuthenticatedUserDetailsOrFail();
        var pageable = PageRequest.ofSize(perPage)
                .withPage(page - 1)
                .withSort(Sort.by(new Order(Direction.DESC, "creationDate")));

        var orders = queryOrderService.getForCustomer(userDetails.getCustomerId(), pageable);

        return ResponseEntity.ok(orderMapper.toPagedOrderListDto(orders));
    }
}
