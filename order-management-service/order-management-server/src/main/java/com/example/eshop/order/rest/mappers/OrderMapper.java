package com.example.eshop.order.rest.mappers;

import com.example.eshop.localizer.Localizer;
import com.example.eshop.order.client.model.AttributeDto;
import com.example.eshop.order.client.model.ImageDto;
import com.example.eshop.order.client.model.OrderDto;
import com.example.eshop.order.client.model.OrderLineDto;
import com.example.eshop.order.client.model.OrderStatusDto;
import com.example.eshop.order.client.model.OrderStatusDto.CodeEnum;
import com.example.eshop.order.client.model.OrderTotalDto;
import com.example.eshop.order.client.model.PagedOrderListDto;
import com.example.eshop.order.domain.order.Order;
import com.example.eshop.order.domain.order.OrderLine;
import com.example.eshop.order.domain.order.OrderLineAttribute;
import com.example.eshop.order.domain.order.OrderStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import java.util.List;
import java.util.stream.Stream;

@Mapper(
        componentModel = "spring",
        uses = { PageableMapper.class, PhoneMapper.class }
)
public abstract class OrderMapper {
    private Localizer localizer;

    // we can't use constructor injection in MapStruct for not @Mapper::uses dependencies
    @Autowired
    public void setLocalizer(Localizer localizer) {
        this.localizer = localizer;
    }

    @Mapping(target = "items", expression = "java(toOrderDtoList(orders.get()))")
    @Mapping(target = "pageable", source = ".")
    public abstract PagedOrderListDto toPagedOrderListDto(Page<Order> orders);

    @Mapping(target = "total", expression = "java(toOrderTotalDto(order))")
    @Mapping(target = "address", source = "delivery.address")
    public abstract OrderDto toOrderDto(Order order);

    protected OrderStatusDto toOrderStatusDto(OrderStatus status) {
        return new OrderStatusDto()
                .code(Enum.valueOf(CodeEnum.class, status.name()))
                .name(localizer.getMessage(status.getMessageCode()));
    }

    protected abstract List<OrderDto> toOrderDtoList(Stream<Order> orders);

    @Mapping(target = "linePrice", expression = "java(line.getPrice())")
    protected abstract OrderLineDto toOrderLineDto(OrderLine line);

    @Mapping(target = "url", source = ".")
    protected abstract ImageDto toImageDto(String image);

    @Mapping(target = "id", source = "attributeId")
    protected abstract AttributeDto toAttributeDto(OrderLineAttribute attr);

    @Mapping(target = "totalPrice", expression = "java(order.getPrice())")
    @Mapping(target = "deliveryPrice", source = "delivery.price")
    protected abstract OrderTotalDto toOrderTotalDto(Order order);
}
