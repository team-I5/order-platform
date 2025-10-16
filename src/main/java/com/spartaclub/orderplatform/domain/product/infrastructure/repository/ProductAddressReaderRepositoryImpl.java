package com.spartaclub.orderplatform.domain.product.infrastructure.repository;

import com.spartaclub.orderplatform.domain.product.domain.repository.ProductAddressReaderRepository;
import com.spartaclub.orderplatform.domain.user.domain.entity.Address;
import com.spartaclub.orderplatform.domain.user.infrastructure.repository.AddressJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ProductAddressReaderRepositoryImpl implements ProductAddressReaderRepository {

    private final AddressJpaRepository addressJpaRepository;


    @Override
    public Optional<Address> findById(UUID addressId) {
        return addressJpaRepository.findById(addressId);
    }
}
