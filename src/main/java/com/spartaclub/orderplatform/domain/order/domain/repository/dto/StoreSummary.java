package com.spartaclub.orderplatform.domain.order.domain.repository.dto;

import com.spartaclub.orderplatform.domain.store.application.facade.dto.StoreView;
import java.util.UUID;

public record StoreSummary(
    UUID storeId,
    String storeName,
    Long ownerId
) {

    public static StoreSummary from(StoreView storeView) {
        return new StoreSummary(
            storeView.storeId(),
            storeView.storeName(),
            storeView.ownerId()
        );
    }
}
