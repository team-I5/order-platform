package com.spartaclub.orderplatform.domain.order.infrastructure.repository;

import com.spartaclub.orderplatform.domain.order.domain.repository.StoreReaderRepository;
import com.spartaclub.orderplatform.domain.order.domain.repository.dto.StoreSummary;
import com.spartaclub.orderplatform.domain.store.application.facade.StorePublicReader;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StoreReaderRepositoryImpl implements StoreReaderRepository {

    private final StorePublicReader storePublicReader;

    @Override
    public Optional<StoreSummary> loadStoreSummaryInfo(UUID storeId) {
        return storePublicReader.loadStoreSummaryInfo(storeId).map(StoreSummary::from);
    }
}
// TODO: StoreJPARepository 생기면 수정하기