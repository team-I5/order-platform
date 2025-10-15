package com.spartaclub.orderplatform.domain.payment.application.dto.query;

import com.spartaclub.orderplatform.domain.payment.domain.model.PaymentStatus;
import java.util.List;

public record PaymentQuery(
    List<PaymentStatus> status
) {

}