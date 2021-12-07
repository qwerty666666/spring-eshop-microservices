package com.example.eshop.rest.mappers;

import com.example.eshop.rest.dto.AttributeDto;
import com.example.eshop.rest.dto.ImageDto;
import com.example.eshop.rest.dto.MoneyDto;
import com.example.eshop.rest.dto.OrderDto;
import com.example.eshop.rest.dto.OrderLineDto;
import com.example.eshop.rest.dto.OrderTotalDto;
import com.example.eshop.rest.dto.PagedOrderListDto;
import com.example.eshop.sales.domain.Order;
import com.example.eshop.sales.domain.OrderLine;
import com.example.eshop.sales.domain.OrderLineAttribute;
import com.example.eshop.sharedkernel.domain.valueobject.Money;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;
import java.util.List;
import java.util.stream.Stream;

@Mapper(
        componentModel = "spring",
        uses = { PageableMapper.class, PhoneMapper.class, EanMapper.class }
)
public interface OrderMapper {
    @Mapping(target = "items", expression = "java(toOrderDtoList(orders.get()))")
    @Mapping(target = "pageable", source = ".")
    PagedOrderListDto toPagedOrderListDto(Page<Order> orders);

    List<OrderDto> toOrderDtoList(Stream<Order> orders);

    @Mapping(target = "total", expression = "java(toOrderTotalDto(order))")
    @Mapping(target = "address", source = "delivery.address")
    OrderDto toOrderDto(Order order);

    @Mapping(target = "linePrice", expression = "java(toMoneyDto(line.getPrice()))")
    OrderLineDto toOrderLineDto(OrderLine line);

    @Mapping(target = "url", source = ".")
    ImageDto toImageDto(String image);

    @Mapping(target = "id", source = "attributeId")
    AttributeDto toAttributeDto(OrderLineAttribute attr);

    @Mapping(target = "totalPrice", expression = "java(toMoneyDto(order.getPrice()))")
    @Mapping(target = "deliveryPrice", source = "delivery.price")
    OrderTotalDto toOrderTotalDto(Order order);

    MoneyDto toMoneyDto(Money money);
}
