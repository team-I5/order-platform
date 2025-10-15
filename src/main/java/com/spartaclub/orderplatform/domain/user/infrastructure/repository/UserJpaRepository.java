package com.spartaclub.orderplatform.domain.user.infrastructure.repository;

import com.spartaclub.orderplatform.domain.user.domain.entity.User;
import com.spartaclub.orderplatform.domain.user.domain.entity.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * User JPA 레포지토리 인터페이스
 * Spring Data JPA 메서드 이름 기반 쿼리 사용으로 간소화
 * Infrastructure 계층에서만 사용
 *
 * @author 전우선
 * @date 2025-10-15
 */
public interface UserJpaRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    // 조회 메서드들 - Spring Data JPA가 메서드 이름으로 자동 쿼리 생성
    Optional<User> findByEmailAndDeletedAtIsNull(String email);
    Optional<User> findByUserIdAndDeletedAtIsNull(Long userId);
    Optional<User> findByUsernameAndDeletedAtIsNull(String username);
    Optional<User> findByNicknameAndDeletedAtIsNull(String nickname);
    Optional<User> findByPhoneNumberAndDeletedAtIsNull(String phoneNumber);
    Optional<User> findByBusinessNumberAndDeletedAtIsNull(String businessNumber);

    // 중복 체크 메서드들
    boolean existsByEmailAndDeletedAtIsNull(String email);
    boolean existsByUsernameAndDeletedAtIsNull(String username);
    boolean existsByNicknameAndDeletedAtIsNull(String nickname);
    boolean existsByPhoneNumberAndDeletedAtIsNull(String phoneNumber);
    boolean existsByBusinessNumberAndDeletedAtIsNull(String businessNumber);

    // 관리자용 회원 목록 조회 메서드
    Page<User> findByDeletedAtIsNull(Pageable pageable);
    Page<User> findByRoleAndDeletedAtIsNull(UserRole role, Pageable pageable);
    Page<User> findByRole(UserRole role, Pageable pageable);
    
    // 키워드 검색
    Page<User> findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrNicknameContainingIgnoreCaseAndDeletedAtIsNull(
        String username, String email, String nickname, Pageable pageable);
    
    // 날짜 범위 조회
    Page<User> findByCreatedAtBetweenAndDeletedAtIsNull(LocalDateTime start, LocalDateTime end, Pageable pageable);

    // 통계용 권한별 회원 수 조회
    @Query("SELECT u.role, COUNT(u) FROM User u WHERE u.deletedAt IS NULL GROUP BY u.role")
    List<Object[]> countByRoleAndActiveUsers();

    // 전체 회원 수 조회
    @Query("SELECT COUNT(u) FROM User u")
    long countAllUsers();

    // 활성 회원 수 조회
    @Query("SELECT COUNT(u) FROM User u WHERE u.deletedAt IS NULL")
    long countActiveUsers();

    // 탈퇴 회원 수 조회
    @Query("SELECT COUNT(u) FROM User u WHERE u.deletedAt IS NOT NULL")
    long countDeletedUsers();
}