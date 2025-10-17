package com.spartaclub.orderplatform.domain.order.domain.repository;

import com.spartaclub.orderplatform.domain.order.domain.repository.dto.StoreSummary;
import java.util.Optional;
import java.util.UUID;

public interface StoreReaderRepository {

    Optional<StoreSummary> loadStoreSummaryInfo(UUID storeId);
}
