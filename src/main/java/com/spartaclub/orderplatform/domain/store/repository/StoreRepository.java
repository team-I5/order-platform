package com.spartaclub.orderplatform.domain.store.repository;

import com.spartaclub.orderplatform.domain.store.entity.Store;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<Store, UUID> {

}
