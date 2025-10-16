package com.spartaclub.orderplatform.domain.order.application.facade;

import com.spartaclub.orderplatform.domain.order.application.facade.dto.OrderView;
import java.util.Optional;
import java.util.UUID;

/*
    공개용 인터페이스(퍼사드)
    다른 도메인에서 Order 관련 데이터는 반드시 이 인터페이스를 통해서만 접근
    내부 로직을 감추고 단일 진입점 제공
 */
public interface OrderPublicReader {

    Optional<OrderView> loadOrderSummaryInfo(UUID orderId);

}
