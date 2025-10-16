package com.spartaclub.orderplatform.domain.product.domain.repository;

import com.spartaclub.orderplatform.domain.user.domain.entity.Address;

import java.util.Optional;
import java.util.UUID;

public interface ProductAddressReaderRepository {
    Optional<Address> findById(UUID addressId);
}
