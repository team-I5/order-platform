package com.spartaclub.orderplatform.domain.payment.infrastructure.repository.spec;

import com.spartaclub.orderplatform.domain.payment.domain.model.Payment;
import com.spartaclub.orderplatform.domain.payment.domain.model.PaymentStatus;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

public final class PaymentSpecs {

    private PaymentSpecs() {
    }

    //상태별 필터
    public static Specification<Payment> statusIn(List<PaymentStatus> statuses) {
        return (root, q, cb) -> {
            if (statuses == null || statuses.isEmpty()) {
                return cb.conjunction();
            }
            var in = cb.in(root.get("status"));
            statuses.forEach(in::value);
            return in;
        };
    }
}
