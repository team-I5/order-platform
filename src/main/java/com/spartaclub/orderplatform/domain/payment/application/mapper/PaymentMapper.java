package com.spartaclub.orderplatform.domain.payment.application.mapper;

import com.spartaclub.orderplatform.domain.payment.domain.model.Payment;
import com.spartaclub.orderplatform.domain.payment.domain.model.PaymentStatus;
import com.spartaclub.orderplatform.domain.payment.presentation.dto.PaymentDetailResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", imports = {PaymentStatus.class})
public interface PaymentMapper {

    //entity -> dto 변환
    @Mapping(target = "orderId", source = "order.orderId")
    PaymentDetailResponseDto toDto(Payment payment);
}
