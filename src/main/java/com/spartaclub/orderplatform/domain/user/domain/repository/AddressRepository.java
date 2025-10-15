package com.spartaclub.orderplatform.domain.user.domain.repository;

import com.spartaclub.orderplatform.domain.user.domain.entity.Address;
import com.spartaclub.orderplatform.domain.user.domain.entity.User;

import java.util.List;
import java.util.Optional;

/**
 * Address 도메인 레포지토리 인터페이스
 * 도메인 계층에서 필요한 Address 관련 데이터 접근 메서드 정의
 * JPA에 의존하지 않는 순수 도메인 인터페이스
 *
 * @author 전우선
 * @date 2025-10-15
 */
public interface AddressRepository {

    // 기본 CRUD
    Address save(Address address);
    Optional<Address> findById(java.util.UUID addressId);
    void delete(Address address);

    // 사용자별 주소 조회
    List<Address> findByUser(User user);
    List<Address> findByUserId(Long userId);

    // 기본 주소 조회
    Optional<Address> findDefaultByUser(User user);
    Optional<Address> findDefaultByUserId(Long userId);
    
    // 주소 중복 체크
    boolean existsByUserAndAddressName(User user, String addressName);
    
    // 주소 개수 조회
    long countByUser(User user);
    
    // 정렬된 주소 목록 조회
    List<Address> findByUserOrderByIsDefaultDescCreatedAtDesc(User user);
    List<Address> findActiveByUserOrderByIsDefaultDescCreatedAtDesc(User user);
    
    // 특정 주소 제외하고 조회 (기본 주소 변경 시 사용)
    List<Address> findActiveByUserExcludingId(User user, java.util.UUID addressId);

    // 페이징 조회
    AddressPage findByUser(User user, AddressPageRequest pageRequest);
    AddressPage findByUserId(Long userId, AddressPageRequest pageRequest);

    /**
     * 주소 페이징 요청 도메인 객체
     */
    record AddressPageRequest(
        int page,
        int size,
        String sortBy,
        boolean ascending
    ) {}

    /**
     * 주소 페이징 결과 도메인 객체
     */
    record AddressPage(
        List<Address> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last
    ) {}
}