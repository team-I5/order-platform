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
    /*
    더 구현 해볼만한 메서드
    List<StoreView> findByOwner(Long ownerId);                     // 오너의 모든 가게
    Page<StoreView> search(StoreSearchQuery query, Pageable pageable); // 검색/페이징
    Optional<StoreDetailView> loadStoreDetail(UUID storeId);       // 더 풍부한 상세 뷰
     */
}
