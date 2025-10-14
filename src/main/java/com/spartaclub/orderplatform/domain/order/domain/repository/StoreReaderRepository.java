package com.spartaclub.orderplatform.domain.order.domain.repository;

import com.spartaclub.orderplatform.domain.store.domain.model.Store;
import java.util.UUID;

public interface StoreReaderRepository {

    Store findById(UUID storeId);
}
