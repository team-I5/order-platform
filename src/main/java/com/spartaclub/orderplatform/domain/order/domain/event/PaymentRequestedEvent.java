package com.spartaclub.orderplatform.domain.order.domain.event;

import java.util.UUID;

public record PaymentRequestedEvent(UUID paymentId, UUID orderId, Long amount) {

}
