package com.spartaclub.orderplatform.domain.order.application.mapper;

import com.spartaclub.orderplatform.domain.order.domain.model.Order;
import com.spartaclub.orderplatform.domain.order.domain.model.OrderProduct;
import com.spartaclub.orderplatform.domain.order.domain.model.OrderStatus;
import com.spartaclub.orderplatform.domain.order.presentation.dto.request.PlaceOrderRequestDto;
import com.spartaclub.orderplatform.domain.order.presentation.dto.response.OrderDetailResponseDto;
import com.spartaclub.orderplatform.domain.order.presentation.dto.response.OrdersResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring", imports = {OrderStatus.class})
public interface OrderMapper {

    // PlaceOrderRequestDto -> Order 변환
    @Mapping(target = "orderId", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "store", ignore = true)
    @Mapping(target = "createdId", source = "userId")
    @Mapping(target = "modifiedId", ignore = true)
    @Mapping(target = "deletedId", ignore = true)
    @Mapping(target = "status", expression = "java(OrderStatus.PAYMENT_PENDING)")
    @Mapping(target = "payment", ignore = true)
    @Mapping(target = "orderProducts", ignore = true)
    Order toEntity(PlaceOrderRequestDto placeOrderRequestDto,
        Long totalPrice,
        Integer productCount,
        Long userId);

    // Order -> OrderDetailResponseDto 변환
    @Mapping(source = "orderProducts", target = "productsList")
    @Mapping(source = "user.userId", target = "userId")
    @Mapping(source = "store.storeId", target = "storeId")
    OrderDetailResponseDto toDto(Order order);

    // OrderProduct -> ProductsListItem
    @Mapping(source = "product.productId", target = "productId")
    @Mapping(source = "productName", target = "name")
    @Mapping(source = "unitPrice", target = "price")
    @Mapping(target = "totalPrice", expression = "java(op.getUnitPrice() * op.getQuantity())")
    OrderDetailResponseDto.ProductsListItem toItem(OrderProduct op);

    // Order -> OrderSummaryDto
    @Mapping(source = "user.userId", target = "userId")
    @Mapping(source = "store.storeId", target = "storeId")
    OrdersResponseDto.OrderSummaryDto toSummaryDto(Order order);

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
