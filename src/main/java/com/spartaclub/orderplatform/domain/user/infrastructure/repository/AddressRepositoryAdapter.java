package com.spartaclub.orderplatform.domain.user.infrastructure.repository;

import com.spartaclub.orderplatform.domain.user.domain.entity.Address;
import com.spartaclub.orderplatform.domain.user.domain.entity.User;
import com.spartaclub.orderplatform.domain.user.domain.repository.AddressDomainRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * AddressDomainRepository의 JPA 구현체
 */
@Component
@RequiredArgsConstructor
public class AddressRepositoryAdapter implements AddressDomainRepository {

    private final AddressRepository addressJpaRepository;

    @Override
    public Address save(Address address) {
        return addressJpaRepository.save(address);
    }

    @Override
    public Optional<Address> findById(UUID addressId) {
        return addressJpaRepository.findById(addressId);
    }

    @Override
    public boolean existsByUserAndAddressNameAndDeletedAtIsNull(User user, String addressName) {
        return addressJpaRepository.existsByUserAndAddressNameAndDeletedAtIsNull(user, addressName);
    }

    @Override
    public Optional<Address> findByUserAndDefaultAddressTrueAndDeletedAtIsNull(User user) {
        return addressJpaRepository.findByUserAndDefaultAddressTrueAndDeletedAtIsNull(user);
    }

    @Override
    public long countByUserAndDeletedAtIsNull(User user) {
        return addressJpaRepository.countByUserAndDeletedAtIsNull(user);
    }

    @Override
    public List<Address> findByUserOrderByDefaultAddressDescCreatedAtDesc(User user) {
        return addressJpaRepository.findByUserOrderByDefaultAddressDescCreatedAtDesc(user);
    }

    @Override
    public List<Address> findByUserAndDeletedAtIsNullOrderByDefaultAddressDescCreatedAtDesc(User user) {
        return addressJpaRepository.findByUserAndDeletedAtIsNullOrderByDefaultAddressDescCreatedAtDesc(user);
    }

    @Override
    public Optional<Address> findByUserAndAddressNameAndDeletedAtIsNull(User user, String addressName) {
        return addressJpaRepository.findByUserAndAddressNameAndDeletedAtIsNull(user, addressName);
    }

    @Override
    public List<Address> findByUserAndDeletedAtIsNullAndAddressIdNotOrderByCreatedAtDesc(User user, UUID excludeAddressId) {
        return addressJpaRepository.findByUserAndDeletedAtIsNullAndAddressIdNotOrderByCreatedAtDesc(user, excludeAddressId);
    }
}