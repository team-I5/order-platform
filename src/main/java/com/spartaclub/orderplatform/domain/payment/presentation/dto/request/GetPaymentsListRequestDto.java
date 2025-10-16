package com.spartaclub.orderplatform.domain.payment.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.spartaclub.orderplatform.domain.payment.domain.model.PaymentStatus;
import java.util.List;

public record GetPaymentsListRequestDto(

    @JsonAlias({"statuses", "status"})
    List<PaymentStatus> status
) {

}
