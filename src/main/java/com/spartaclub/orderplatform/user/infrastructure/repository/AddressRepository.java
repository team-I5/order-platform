package com.spartaclub.orderplatform.user.infrastructure.repository;

import com.spartaclub.orderplatform.user.domain.entity.Address;
import com.spartaclub.orderplatform.user.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 주소 레포지토리 인터페이스
 * 주소 데이터 접근을 위한 JPA 레포지토리
 *
 * @author 전우선
 * @date 2025-10-12(일)
 */
@Repository
public interface AddressRepository extends JpaRepository<Address, UUID> {

    // 사용자별 활성 주소 목록 조회 (기본 주소 우선, 생성일시 최신순)
    @Query("SELECT a FROM Address a WHERE a.user = :user AND a.deletedAt IS NULL " +
            "ORDER BY a.defaultAddress DESC, a.createdAt DESC")
    List<Address> findByUserAndDeletedAtIsNullOrderByDefaultAddressDescCreatedAtDesc(@Param("user") User user);

    // 사용자별 모든 주소 목록 조회 (삭제된 주소 포함)
    @Query("SELECT a FROM Address a WHERE a.user = :user " +
            "ORDER BY a.defaultAddress DESC, a.createdAt DESC")
    List<Address> findByUserOrderByDefaultAddressDescCreatedAtDesc(@Param("user") User user);

    // 사용자별 기본 주소 조회
    Optional<Address> findByUserAndDefaultAddressTrueAndDeletedAtIsNull(User user);

    // 사용자별 주소명 중복 체크 (활성 주소만)
    boolean existsByUserAndAddressNameAndDeletedAtIsNull(User user, String addressName);

    // 사용자별 활성 주소 개수 조회
    long countByUserAndDeletedAtIsNull(User user);

    // 특정 주소 조회 (소유자 및 삭제 여부 확인)
    Optional<Address> findByAddressIdAndUserAndDeletedAtIsNull(UUID addressId, User user);

    // 주소명으로 주소 조회 (중복 체크용)
    Optional<Address> findByUserAndAddressNameAndDeletedAtIsNull(User user, String addressName);

    // 특정 주소 ID를 제외한 사용자의 활성 주소 목록 조회 (생성일시 최신순)
    List<Address> findByUserAndDeletedAtIsNullAndAddressIdNotOrderByCreatedAtDesc(User user, UUID excludeAddressId);
}