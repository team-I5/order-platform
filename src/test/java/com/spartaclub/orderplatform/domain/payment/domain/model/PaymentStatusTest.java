package com.spartaclub.orderplatform.domain.payment.domain.model;

import static com.spartaclub.orderplatform.domain.payment.domain.model.PaymentStatus.AUTHORIZED;
import static com.spartaclub.orderplatform.domain.payment.domain.model.PaymentStatus.CAPTURED;
import static com.spartaclub.orderplatform.domain.payment.domain.model.PaymentStatus.FAILED;
import static com.spartaclub.orderplatform.domain.payment.domain.model.PaymentStatus.PAYMENT_PENDING;
import static com.spartaclub.orderplatform.domain.payment.domain.model.PaymentStatus.REFUNDED;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class PaymentStatusTest {

    @Test
    void helpers_workAsExpected() {
        assertThat(PAYMENT_PENDING.isPending()).isTrue();
        assertThat(AUTHORIZED.isAuthorized()).isTrue();
        assertThat(CAPTURED.isCaptured()).isTrue();
        assertThat(FAILED.isFailed()).isTrue();
        assertThat(REFUNDED.isRefunded()).isTrue();

        assertThat(CAPTURED.isNotAuthorized()).isTrue();
        assertThat(AUTHORIZED.isNotCaptured()).isTrue();
    }
}
