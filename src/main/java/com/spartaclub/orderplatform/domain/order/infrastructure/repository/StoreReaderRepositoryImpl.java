package com.spartaclub.orderplatform.domain.order.infrastructure.repository;

import com.spartaclub.orderplatform.domain.order.domain.repository.StoreReaderRepository;
import com.spartaclub.orderplatform.domain.store.domain.model.Store;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class StoreReaderRepositoryImpl implements StoreReaderRepository {

    //private final StoreJPARepository storeJPARepository;

    @Override
    public Optional<Store> findById(UUID storeId) {
        return Optional.empty();
    }
}
// TODO: StoreJPARepository 생기면 수정하기