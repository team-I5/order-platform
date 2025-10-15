package com.spartaclub.orderplatform.domain.payment.application.mapper;

import com.spartaclub.orderplatform.domain.payment.domain.model.Payment;
import com.spartaclub.orderplatform.domain.payment.domain.model.PaymentStatus;
import com.spartaclub.orderplatform.domain.payment.presentation.dto.response.PaymentDetailResponseDto;
import com.spartaclub.orderplatform.domain.payment.presentation.dto.response.PaymentsListResponseDto;
import com.spartaclub.orderplatform.domain.payment.presentation.dto.response.PaymentsListResponseDto.PageableDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring", imports = {PaymentStatus.class})
public interface PaymentMapper {

    //entity -> dto 변환
    @Mapping(target = "orderId", source = "order.orderId")
    PaymentDetailResponseDto toDto(Payment payment);

    // Page -> PageableDto
    default PageableDto toPageableDto(Page<?> page) {
        return new PaymentsListResponseDto.PageableDto(
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
