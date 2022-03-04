package com.example.eshop.order.rest.mappers;

import com.example.eshop.localizer.Localizer;
import com.example.eshop.order.client.api.model.AttributeDto;
import com.example.eshop.order.client.api.model.ImageDto;
import com.example.eshop.order.client.api.model.MoneyDto;
import com.example.eshop.order.client.api.model.OrderDto;
import com.example.eshop.order.client.api.model.OrderLineDto;
import com.example.eshop.order.client.api.model.OrderStatusDto;
import com.example.eshop.order.client.api.model.OrderStatusDto.CodeEnum;
import com.example.eshop.order.client.api.model.OrderTotalDto;
import com.example.eshop.order.client.api.model.PagedOrderListDto;
import com.example.eshop.order.domain.order.Order;
import com.example.eshop.order.domain.order.OrderLine;
import com.example.eshop.order.domain.order.OrderLineAttribute;
import com.example.eshop.order.domain.order.OrderStatus;
import com.example.eshop.sharedkernel.domain.valueobject.Money;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import java.util.List;
import java.util.stream.Stream;

@Mapper(
        componentModel = "spring",
        uses = { PageableMapper.class, PhoneMapper.class, EanMapper.class }
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

    @Mapping(target = "linePrice", expression = "java(toMoneyDto(line.getPrice()))")
    protected abstract OrderLineDto toOrderLineDto(OrderLine line);

    @Mapping(target = "url", source = ".")
    protected abstract ImageDto toImageDto(String image);

    @Mapping(target = "id", source = "attributeId")
    protected abstract AttributeDto toAttributeDto(OrderLineAttribute attr);

    @Mapping(target = "totalPrice", expression = "java(toMoneyDto(order.getPrice()))")
    @Mapping(target = "deliveryPrice", source = "delivery.price")
    protected abstract OrderTotalDto toOrderTotalDto(Order order);

    protected abstract MoneyDto toMoneyDto(Money money);
}
