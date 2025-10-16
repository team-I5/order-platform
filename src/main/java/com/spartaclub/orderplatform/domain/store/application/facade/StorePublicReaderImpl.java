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
        return storeRepository.findById(storeId)
            .map(StoreView::from);
    }
}
