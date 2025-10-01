package com.spartaclub.orderplatform.domain.order.application.mapper;

import com.spartaclub.orderplatform.domain.order.domain.model.Order;
import com.spartaclub.orderplatform.domain.order.domain.model.OrderStatus;
import com.spartaclub.orderplatform.domain.order.presentation.dto.PlaceOrderRequestDto;
import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", imports = {OrderStatus.class})
public interface OrderMapper {

    // Dto -> Entity 변환
    @Mapping(target = "orderId", ignore = true)
//    @Mapping(target = "userId", ignore = true)
//    @Mapping(target = "storeId", ignore = true)
    @Mapping(target = "createdId", ignore = true)
    @Mapping(target = "modifiedId", ignore = true)
    @Mapping(target = "deletedId", ignore = true)
    @Mapping(target = "status", expression = "java(OrderStatus.PAYMENT_PENDING)")
    @Mapping(target = "payment", ignore = true)
    Order toEntity(PlaceOrderRequestDto placeOrderRequestDto, Long totalPrice,
        Integer productCount, UUID storeId, Long userId);
}
