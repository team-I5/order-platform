package com.spartaclub.orderplatform.domain.order.application.dto;

import com.spartaclub.orderplatform.domain.order.domain.repository.dto.StoreSummary;
import java.util.UUID;

public record StoreInfoDto(
    UUID storeId,
    String storeName,
    Long ownerId
) {

    public static StoreInfoDto from(StoreSummary store) {
        return new StoreInfoDto(
            store.storeId(),
            store.storeName(),
            store.ownerId()
        );
    }
}
