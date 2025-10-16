package com.spartaclub.orderplatform.domain.store.application.facade.dto;

import com.spartaclub.orderplatform.domain.store.domain.model.Store;
import java.util.UUID;

public record StoreView(
    UUID storeId,
    String storeName,
    Long ownerId
) {

    public static StoreView from(Store store) {
        return new StoreView(
            store.getStoreId(),
            store.getStoreName(),
            store.getUser().getUserId());
    }
}
