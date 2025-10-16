package com.spartaclub.orderplatform.domain.order.application.mapper;

import com.spartaclub.orderplatform.domain.order.application.command.PlaceOrderCommand;
import com.spartaclub.orderplatform.domain.order.domain.model.Order;
import com.spartaclub.orderplatform.domain.order.domain.model.OrderProduct;
import com.spartaclub.orderplatform.domain.order.domain.model.OrderStatus;
import com.spartaclub.orderplatform.domain.order.presentation.dto.request.PlaceOrderRequestDto.OrderItemRequest;
import com.spartaclub.orderplatform.domain.order.presentation.dto.response.OrderDetailResponseDto;
import com.spartaclub.orderplatform.domain.order.presentation.dto.response.OrdersResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring", imports = {OrderStatus.class})
public interface OrderMapper {

    // Order -> OrderDetailResponseDto 변환
    @Mapping(source = "orderProducts", target = "productsList")
    @Mapping(source = "user.userId", target = "userId")
    @Mapping(source = "storeId", target = "storeId")
    OrderDetailResponseDto toDto(Order order);

    // OrderProduct -> ProductsListItem
    @Mapping(source = "product.productId", target = "productId")
    @Mapping(source = "productName", target = "name")
    @Mapping(source = "unitPrice", target = "price")
    @Mapping(target = "totalPrice", expression = "java(op.getUnitPrice() * op.getQuantity())")
    OrderDetailResponseDto.ProductsListItem toItem(OrderProduct op);

    // Order -> OrderSummaryDto
    @Mapping(source = "user.userId", target = "userId")
    @Mapping(source = "storeId", target = "storeId")
    OrdersResponseDto.OrderSummaryDto toSummaryDto(Order order);

    PlaceOrderCommand toCommand(OrderItemRequest requestDto);

    // Page -> PageableDto
    default OrdersResponseDto.PageableDto toPageableDto(Page<?> page) {
        return new OrdersResponseDto.PageableDto(
            page.getNumber() + 1,
            page.getSize(),
            (int) page.getTotalElements(),
            page.getTotalPages(),
            page.hasNext(),
            page.hasPrevious(),
            page.isFirst(),
            page.isLast()
        );
    }
}
