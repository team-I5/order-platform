package com.spartaclub.orderplatform.domain.user.infrastructure.repository;

import com.spartaclub.orderplatform.domain.user.domain.entity.Address;
import com.spartaclub.orderplatform.domain.user.domain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Address JPA 레포지토리 인터페이스
 * Infrastructure 계층에서만 사용
 *
 * @author 전우선
 * @date 2025-10-15
 */
public interface AddressJpaRepository extends JpaRepository<Address, java.util.UUID> {

    List<Address> findByUser(User user);
    List<Address> findByUserUserId(Long userId);
    
    Optional<Address> findByUserAndIsDefaultTrue(User user);
    Optional<Address> findByUserUserIdAndIsDefaultTrue(Long userId);
    
    Page<Address> findByUser(User user, Pageable pageable);
    Page<Address> findByUserUserId(Long userId, Pageable pageable);
    
    // 주소 중복 체크
    boolean existsByUserAndAddressName(User user, String addressName);
    
    // 주소 개수 조회
    long countByUser(User user);
    
    // 정렬된 주소 목록 조회
    List<Address> findByUserOrderByIsDefaultDescCreatedAtDesc(User user);
    
    // 특정 주소 제외하고 조회
    List<Address> findByUserAndAddressIdNotOrderByCreatedAtDesc(User user, java.util.UUID addressId);
}