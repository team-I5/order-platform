package com.spartaclub.orderplatform.domain.order.presentation.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import org.springframework.web.bind.annotation.PathVariable;

public record GetOrderDetailRequestDto(
    @PathVariable("orderId")
    @NotNull(message = "orderId는 필수 값입니다.")
    UUID orderId
) {

}
