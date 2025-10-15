package com.spartaclub.orderplatform.domain.user.domain.repository;

import com.spartaclub.orderplatform.domain.user.domain.entity.User;
import com.spartaclub.orderplatform.domain.user.domain.entity.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * User 도메인 레포지토리 인터페이스
 * 도메인 계층의 추상화된 저장소 인터페이스
 * JPA 구현 세부사항을 숨기고 도메인 관점의 메서드만 노출
 */
public interface UserDomainRepository {

    // 기본 CRUD
    User save(User user);
    Optional<User> findById(Long userId);
    void delete(User user);

    // 도메인 관점의 조회 메서드
    Optional<User> findActiveUserByEmail(String email);
    Optional<User> findActiveUserById(Long userId);
    Optional<User> findActiveUserByUsername(String username);
    Optional<User> findActiveUserByNickname(String nickname);
    Optional<User> findActiveUserByPhoneNumber(String phoneNumber);
    Optional<User> findActiveUserByBusinessNumber(String businessNumber);

    // 도메인 관점의 중복 검증
    boolean isEmailAlreadyTaken(String email);
    boolean isUsernameAlreadyTaken(String username);
    boolean isNicknameAlreadyTaken(String nickname);
    boolean isPhoneNumberAlreadyTaken(String phoneNumber);
    boolean isBusinessNumberAlreadyTaken(String businessNumber);

    // 관리자용 조회
    Page<User> findActiveUsers(Pageable pageable);
    Page<User> findAllUsers(Pageable pageable);
    Page<User> findActiveUsersByRole(UserRole role, Pageable pageable);
    Page<User> findAllUsersByRole(UserRole role, Pageable pageable);
    Page<User> searchActiveUsers(String keyword, Pageable pageable);
    Page<User> findActiveUsersByDateRange(LocalDateTime start, LocalDateTime end, Pageable pageable);

    // 통계 정보
    Map<UserRole, Long> getActiveUserCountByRole();
    long getTotalUserCount();
    long getActiveUserCount();
    long getDeletedUserCount();
}