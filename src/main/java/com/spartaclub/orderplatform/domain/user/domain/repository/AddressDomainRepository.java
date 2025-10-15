package com.spartaclub.orderplatform.domain.user.domain.repository;

import com.spartaclub.orderplatform.domain.user.domain.entity.Address;
import com.spartaclub.orderplatform.domain.user.domain.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Address 도메인 레포지토리 인터페이스 도메인 계층의 추상화된 저장소 인터페이스
 */
public interface AddressDomainRepository {

    Address save(Address address);

    Optional<Address> findById(UUID addressId);

    boolean existsByUserAndAddressNameAndDeletedAtIsNull(User user, String addressName);

    Optional<Address> findByUserAndDefaultAddressTrueAndDeletedAtIsNull(User user);

    long countByUserAndDeletedAtIsNull(User user);

    List<Address> findByUserOrderByDefaultAddressDescCreatedAtDesc(User user);

    List<Address> findByUserAndDeletedAtIsNullOrderByDefaultAddressDescCreatedAtDesc(User user);

    Optional<Address> findByUserAndAddressNameAndDeletedAtIsNull(User user, String addressName);

    List<Address> findByUserAndDeletedAtIsNullAndAddressIdNotOrderByCreatedAtDesc(User user,
        UUID excludeAddressId);
}