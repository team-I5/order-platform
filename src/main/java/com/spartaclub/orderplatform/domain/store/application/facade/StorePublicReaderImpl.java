package com.spartaclub.orderplatform.domain.store.application.facade;

import com.spartaclub.orderplatform.domain.store.application.facade.dto.StoreView;
import com.spartaclub.orderplatform.domain.store.domain.repository.StoreRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StorePublicReaderImpl implements StorePublicReader {

    private final StoreRepository storeRepository;

    @Override
    public Optional<StoreView> loadStoreSummaryInfo(UUID storeId) {
        /*퍼사드 계층에서 할 수 있는 것들
          도메인 간 접근 제어
          제공 데이터 필터링
        * */
        return storeRepository.findById(storeId)
            .map(StoreView::from);
    }
}
