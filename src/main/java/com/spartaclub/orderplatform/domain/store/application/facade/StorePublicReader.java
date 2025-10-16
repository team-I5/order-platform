package com.spartaclub.orderplatform.domain.store.application.facade;

import com.spartaclub.orderplatform.domain.store.application.facade.dto.StoreView;
import java.util.Optional;
import java.util.UUID;

/*
    공개용 인터페이스(퍼사드)
    다른 도메인에서 Store 관련 데이터는 반드시 이 인터페이스를 통해서만 접근
    내부 로직을 감추고 단일 진입점 제공
 */
public interface StorePublicReader {

    Optional<StoreView> loadStoreSummaryInfo(UUID storeId);
}
